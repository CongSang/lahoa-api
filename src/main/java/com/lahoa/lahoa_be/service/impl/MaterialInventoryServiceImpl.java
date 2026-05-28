package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.MaterialInventoryMapper;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialInventoryRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.MaterialInventoryMovementService;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import com.lahoa.lahoa_be.specification.MaterialInventorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialInventoryServiceImpl implements MaterialInventoryService {

    private final MaterialInventoryRepository inventoryRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryMapper inventoryMapper;
    private final PagedMapper pagedMapper;
    private final MaterialInventoryMovementService movementService;

    private MaterialInventoryEntity getOrCreate(InventoryActionRequestDTO req) {
        return inventoryRepository
                .findByMaterialIdAndWarehouseId(
                        req.getMaterialId(),
                        req.getWarehouseId()
                )
                .orElseGet(() ->
                    inventoryRepository.save(
                        MaterialInventoryEntity
                            .builder()
                            .material(materialRepository
                                .findById(req.getMaterialId())
                                .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Không tìm thấy nguyên liệu"
                                    )
                                )
                            )
                            .warehouse(warehouseRepository
                                .findById(req.getWarehouseId())
                                .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Không tìm thấy kho"
                                    )
                                )
                            )
                            .onHand(0)
                            .reserved(0)
                            .build()
                    )
                );
    }

    @Override
    public void importStock(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getOrCreate(req);

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        updateWeightedCost(
                inventory,
                beforeOnHand,
                req.getQuantity(),
                req.getUnitCost()
        );

        inventory.setOnHand(inventory.getOnHand() + req.getQuantity());

        movementService.log(
                inventory,
                InventoryMovementType.IMPORT,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );

        inventoryMapper.toDTO(inventory);
    }

    @Override
    public void adjustStock(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getOrCreate(req);

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setOnHand(req.getQuantity());

        movementService.log(
                inventory,
                InventoryMovementType.IMPORT,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );

        inventoryMapper.toDTO(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MaterialInventorySummaryResponseDTO> list(MaterialInventoryFilterRequestDTO filter) {
        Specification<MaterialEntity> spec = MaterialInventorySpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<MaterialEntity> invPaged = materialRepository.findAll(spec, pageable);

        List<MaterialInventorySummaryResponseDTO> dtoList = invPaged.getContent()
                .stream().map(inventoryMapper::mapSummary).toList();

        return pagedMapper.toDTO(invPaged, dtoList);
    }

    private void updateWeightedCost(
            MaterialInventoryEntity inventory,
            int currentQty,
            int importQty,
            BigDecimal importCost
    ) {
        BigDecimal currentAvg =
                Optional.ofNullable(
                        inventory.getCostPrice()
                ).orElse(
                        BigDecimal.ZERO
                );

        BigDecimal totalOld = currentAvg.multiply(BigDecimal.valueOf(currentQty));

        BigDecimal totalNew = importCost.multiply(BigDecimal.valueOf(importQty));

        int totalQty = currentQty + importQty;

        BigDecimal avg =
                totalQty == 0
                        ? BigDecimal.ZERO
                        : totalOld.add(totalNew)
                        .divide(
                                BigDecimal.valueOf(totalQty),
                                2,
                                RoundingMode.HALF_UP
                        );

        inventory.setCostPrice(avg);
    }
}
