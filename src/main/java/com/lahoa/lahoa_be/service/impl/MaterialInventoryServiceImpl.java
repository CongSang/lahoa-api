package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialWarehouseInventoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialInventoryRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.MaterialInventoryMovementService;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PagedMapper pagedMapper;
    private final MaterialInventoryMovementService movementService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MaterialInventorySummaryResponseDTO> list(MaterialInventoryFilterRequestDTO filter) {
        String sortField = filter.getSortField();

        if (sortField.equals("categoryName")) {
            sortField = "category.name";
        }

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();



        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<MaterialInventorySummaryResponseDTO> invPaged = inventoryRepository
                .getInventorySummary(
                        filter.getKeyword(),
                        filter.getCategoryId(),
                        filter.getStatus(),
                        filter.getWarehouseId(),
                        filter.getLowStock(),
                        filter.getOutOfStock(),
                        pageable
                );

        List<MaterialInventorySummaryResponseDTO> dtoList = invPaged.getContent();

        return pagedMapper.toDTO(invPaged, dtoList);
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
    }

    @Override
    public void adjustStock(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getOrCreate(req);

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        int afterOnHand = req.getQuantity();

        int deviationAmount = afterOnHand - beforeOnHand;

        inventory.setOnHand(afterOnHand);

        movementService.log(
                inventory,
                InventoryMovementType.ADJUST,
                deviationAmount,
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );
    }

    @Override
    public void reserve(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getInventory(req);

        validateAvailable(inventory, req.getQuantity());

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setReserved(inventory.getReserved() + req.getQuantity());

        movementService.log(
                inventory,
                InventoryMovementType.RESERVE,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );
    }

    @Override
    public void release(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getInventory(req);

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setReserved(
                Math.max(0, inventory.getReserved() - req.getQuantity())
        );

        movementService.log(
                inventory,
                InventoryMovementType.RELEASE,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );
    }

    @Override
    public void consume(InventoryActionRequestDTO req) {
        var inventory = getInventory(req);

        validateAvailable(inventory, req.getQuantity());

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setOnHand(inventory.getOnHand() - req.getQuantity());

        inventory.setReserved(
                Math.max(0, inventory.getReserved() - req.getQuantity())
        );

        movementService.log(
                inventory,
                InventoryMovementType.CONSUME,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialWarehouseInventoryResponseDTO> getWarehouseInventories(
            Long materialId
    ) {
        MaterialEntity material = materialRepository.findById(materialId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu"));

        List<MaterialInventoryEntity> inventories =
                inventoryRepository.findByMaterialId(materialId);

        return inventories.stream()
                .map(i -> {
                    int available = i.getOnHand() - i.getReserved();

                    boolean lowStock = available <= material.getLowStockThreshold();

                    boolean outOfStock = available <= 0;

                    return
                            MaterialWarehouseInventoryResponseDTO
                                    .builder()
                                    .inventoryId(i.getId())
                                    .warehouseId(i.getWarehouse().getId())
                                    .warehouseName(i.getWarehouse().getName())
                                    .onHand(i.getOnHand())
                                    .reserved(i.getReserved())
                                    .available(available)
                                    .costPrice(i.getCostPrice())
                                    .lowStock(lowStock)
                                    .outOfStock(outOfStock)
                                    .updatedAt(i.getUpdatedAt())
                                    .build();
                })
                .toList();
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

    private void validateAvailable(MaterialInventoryEntity inv, int qty) {
        if (inv.getOnHand() - inv.getReserved() < qty) {
            throw new BadRequestException(
                    "Không đủ tồn kho"
            );
        }
    }

    private MaterialInventoryEntity getInventory(InventoryActionRequestDTO req) {
        return inventoryRepository
                .findByMaterialIdAndWarehouseId(
                        req.getMaterialId(),
                        req.getWarehouseId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy tồn kho"
                        )
                );
    }
}
