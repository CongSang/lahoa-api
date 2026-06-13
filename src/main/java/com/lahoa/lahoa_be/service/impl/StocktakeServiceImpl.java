package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.CodePrefix;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.StocktakeFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.CreateStocktakeDetailRequestDTO;
import com.lahoa.lahoa_be.dto.request.CreateStocktakeRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeMaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeResponseDTO;
import com.lahoa.lahoa_be.entity.*;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.StocktakeMapper;
import com.lahoa.lahoa_be.repository.MaterialInventoryRepository;
import com.lahoa.lahoa_be.repository.StocktakeRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.CodeGeneratorService;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import com.lahoa.lahoa_be.service.StocktakeService;
import com.lahoa.lahoa_be.specification.StocktakeSpecification;
import com.lahoa.lahoa_be.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StocktakeServiceImpl implements StocktakeService {

    private final WarehouseRepository warehouseRepository;
    private final CodeGeneratorService codeService;
    private final MaterialInventoryRepository inventoryRepository;
    private final StocktakeRepository stocktakeRepository;
    private final MaterialInventoryService inventoryService;
    private final StocktakeMapper stocktakeMapper;
    private final PagedMapper pagedMapper;

    @Override
    public StocktakeResponseDTO create(CreateStocktakeRequestDTO req) {
        WarehouseEntity warehouse =
                warehouseRepository.findById(req.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Không tìm thấy kho"
                                )
                        );

        StocktakeEntity stocktake =
                StocktakeEntity.builder()
                        .code(codeService.nextWithDate(CodePrefix.STK))
                        .warehouse(warehouse)
                        .note(req.getNote())
                        .createdBy(SecurityUtils.getCurrentUser())
                        .details(new ArrayList<>())
                        .build();

        stocktake = stocktakeRepository.save(stocktake);

        for (CreateStocktakeDetailRequestDTO d : req.getDetails()) {
            MaterialInventoryEntity inventory =
                    inventoryRepository
                            .findByMaterialIdAndWarehouseId(
                                    d.getMaterialId(),
                                    req.getWarehouseId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Không tìm thấy tồn kho của vật liệu"
                                    )
                            );

            int systemQty = inventory.getOnHand();

            int actualQty = d.getActualQty();

            int diff = actualQty - systemQty;

            if (diff == 0) {
                continue;
            }

            StocktakeDetailEntity detail =
                    StocktakeDetailEntity.builder()
                            .stocktake(stocktake)
                            .material(inventory.getMaterial())
                            .systemQty(systemQty)
                            .actualQty(actualQty)
                            .difference(diff)
                            .build();

            stocktake.getDetails().add(detail);

            inventoryService.adjustStock(
                    InventoryActionRequestDTO
                            .builder()
                            .materialId(d.getMaterialId())
                            .warehouseId(req.getWarehouseId())
                            .quantity(d.getActualQty())
                            .unitCost(null)
                            .note("Kiểm kê kho " + stocktake.getCode() + ": " + req.getNote())
                            .referenceType(InventoryReferenceType.STOCKTAKE)
                            .referenceId(stocktake.getId())
                            .referenceCode(stocktake.getCode())
                            .build()
            );
        }

        return stocktakeMapper.toDTO(stocktakeRepository.save(stocktake));
    }

    @Override
    @Transactional(readOnly = true)
    public StocktakeResponseDTO detail(Long id) {
        return stocktakeMapper.toDTO(
                stocktakeRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Không tìm thấy phiếu kiểm kê"
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<StocktakeResponseDTO> list(StocktakeFilterRequestDTO filter) {
        Specification<StocktakeEntity> spec = StocktakeSpecification.filter(filter);
        String sortField = filter.getSortField();

        if (sortField.equals("warehouseName")) {
            sortField = "warehouse.name";
        }

        if (sortField.equals("totalItems")) {
            sortField = "details";
        }

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<StocktakeEntity> paged = stocktakeRepository.findAll(spec, pageable);

        List<StocktakeResponseDTO> dtoList = paged.getContent()
                .stream().map(stocktakeMapper::toListDTO).toList();

        return pagedMapper.toDTO(paged, dtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StocktakeMaterialResponseDTO> getMaterialsForStocktake(
            Long warehouseId
    ) {
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy kho"
                        )
                );

        return inventoryRepository
                .findStocktakeMaterials(
                        warehouseId,
                        Status.DELETED
                )
                .stream()
                .map(i ->
                        StocktakeMaterialResponseDTO
                                .builder()
                                .materialId(i.getMaterial().getId())
                                .materialCode(i.getMaterial().getCode())
                                .materialName(i.getMaterial().getName())
                                .unit(i.getMaterial().getUnit())
                                .systemQty(i.getOnHand())
                                .actualQty(i.getOnHand())
                                .build()
                )
                .toList();
    }
}
