package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.WarehouseFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.WarehouseRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.error.MaterialStockInfoDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.WarehouseResponseDTO;
import com.lahoa.lahoa_be.entity.WarehouseEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.WarehouseMapper;
import com.lahoa.lahoa_be.repository.MaterialInventoryRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.AuditLogService;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import com.lahoa.lahoa_be.service.WarehouseService;
import com.lahoa.lahoa_be.specification.WarehouseSpecification;
import com.lahoa.lahoa_be.util.CompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository inventoryRepository;
    private final WarehouseMapper warehouseMapper;
    private final PagedMapper pagedMapper;
    private final AuditLogService auditService;
    private final MaterialInventoryService inventoryService;

    private void validateCode(String code, Long excludeId) {

        warehouseRepository
                .findByCode(code)
                .ifPresent(
                        wh -> {
                            if (!wh.getId().equals(excludeId)) {
                                throw new BadRequestException(
                                        "Mã kho đã tồn tại"
                                );
                            }
                        }
                );
    }

    private WarehouseEntity getEntity(Long id) {
        WarehouseEntity warehouse = warehouseRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy kho"
                        )
                );

        if (warehouse.getStatus() == Status.DELETED) {
            throw new BadRequestException("Kho đã bị xóa");
        }

        return warehouse;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<WarehouseResponseDTO> list(WarehouseFilterRequestDTO filter) {
        Specification<WarehouseEntity> spec = WarehouseSpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<WarehouseEntity> whPaged = warehouseRepository.findAll(spec, pageable);

        List<Long> ids = whPaged.getContent()
                        .stream()
                        .map(WarehouseEntity::getId)
                        .toList();

        Map<Long, Long> countMap = inventoryRepository
                .countMaterialsByWarehouseIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        List<WarehouseResponseDTO> dtoList = whPaged.getContent()
                .stream().map(wh ->
                        warehouseMapper.toDTO(
                                wh,
                                countMap.getOrDefault(wh.getId(), 0L)
                        )).toList();

        return pagedMapper.toDTO(whPaged, dtoList);
    }

    @Override
    public List<DropdownResponseDTO> getDropdown() {
        List<WarehouseEntity> warehouses = warehouseRepository.findAllByStatus(Status.ACTIVE);

        return warehouses.stream()
                .map(warehouseMapper::toDropdown)
                .toList();
    };

    @Override
    public WarehouseResponseDTO create(WarehouseRequestDTO req) {
        validateCode(req.getCode(), null);

        WarehouseEntity entity = warehouseMapper.toEntity(req);

        WarehouseEntity saved = warehouseRepository.save(entity);

        WarehouseResponseDTO newWarehouse = warehouseMapper.toDTO(saved, 0L);

        auditService.logAfterCommit(
                AuditAction.CREATE,
                AuditEntityType.WAREHOUSE,
                saved.getId(),
                saved.getName(),
                null,
                newWarehouse,
                null
        );

        log.info("Created Warehouse id={}", saved.getId());

        return newWarehouse;
    }

    @Override
    public WarehouseResponseDTO update(Long id, WarehouseRequestDTO req) {
        validateCode(req.getCode(), id);

        WarehouseEntity entity = getEntity(id);
        Long materialCount = countMaterial(entity.getId());

        WarehouseResponseDTO oldWarehouse = warehouseMapper.toDTO(entity, materialCount);


        entity.setCode(req.getCode());
        entity.setName(req.getName());
        entity.setAddress(req.getAddress());
        entity.setStatus(req.getStatus());

        WarehouseEntity saved = warehouseRepository.save(entity);

        WarehouseResponseDTO newWarehouse = warehouseMapper.toDTO(saved, materialCount);

        Map<String, Object> changed =
                CompareUtils.diff(oldWarehouse, newWarehouse);

        if (!changed.isEmpty()) {
            auditService.logAfterCommit(
                    AuditAction.UPDATE,
                    AuditEntityType.WAREHOUSE,
                    saved.getId(),
                    saved.getName(),
                    null,
                    null,
                    changed
            );
        }

        log.info("Updated Warehouse id={}", id);

        return newWarehouse;
    }

    @Override
    public WarehouseResponseDTO updateStatus(Long id) {
        WarehouseEntity entity = getEntity(id);
        Long materialCount = countMaterial(entity.getId());

        WarehouseResponseDTO oldWarehouse = warehouseMapper.toDTO(entity, materialCount);

        entity.setStatus(
                entity.getStatus() ==
                        Status.ACTIVE
                        ? Status.INACTIVE
                        : Status.ACTIVE
        );

        WarehouseEntity saved = warehouseRepository.save(entity);

        WarehouseResponseDTO newWarehouse = warehouseMapper.toDTO(saved, materialCount);

        Map<String, Object> changed =
                CompareUtils.diff(oldWarehouse, newWarehouse);

        if (!changed.isEmpty()) {
            auditService.logAfterCommit(
                    AuditAction.UPDATE,
                    AuditEntityType.WAREHOUSE,
                    saved.getId(),
                    saved.getName(),
                    null,
                    null,
                    changed
            );
        }

        log.info("Changed status Material id={} to {}", id, newWarehouse.getStatus());

        return newWarehouse;
    }

    @Override
    public void delete(Long id) {
        WarehouseEntity entity = getEntity(id);

        List<MaterialStockInfoDTO> remainingStocks =
                inventoryService.getStockByWarehouseId(id);

        if (!remainingStocks.isEmpty()) {
            throw new BadRequestException(
                    buildStockMessage(
                            entity,
                            remainingStocks
                    )
            );
        }

        entity.setStatus(Status.DELETED);

        auditService.logAfterCommit(
                AuditAction.DELETE,
                AuditEntityType.WAREHOUSE,
                entity.getId(),
                entity.getName(),
                null,
                null,
                null
        );

        log.info("Soft deleted Warehouse id={}", id);
    }

    @Override
    public WarehouseResponseDTO restore(Long id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

        if (warehouse.getStatus() != Status.DELETED) {
            throw new BadRequestException("Kho chưa bị xóa");
        }

        warehouse.setStatus(Status.ACTIVE);

        WarehouseEntity saved = warehouseRepository.save(warehouse);

        auditService.logAfterCommit(
                AuditAction.RESTORE,
                AuditEntityType.WAREHOUSE,
                saved.getId(),
                saved.getName(),
                null,
                null,
                null
        );

        log.info("Restored Warehouse id={}", id);

        return warehouseMapper.toDTO(saved, countMaterial(saved.getId()));
    }

    private Long countMaterial(Long id) {
        return inventoryRepository.countMaterialsByWarehouseId(id);
    }

    private String buildStockMessage(
            WarehouseEntity warehouse,
            List<MaterialStockInfoDTO> stocks
    ) {

        StringBuilder sb = new StringBuilder();

        sb.append("Không thể xóa kho ")
                .append(warehouse.getName())
                .append(".\n\n");

        for (MaterialStockInfoDTO stock : stocks) {
            sb.append("- ")
                    .append(stock.getWarehouseName())
                    .append(": còn ")
                    .append(stock.getOnHand());

            if (stock.getReserved() > 0) {
                sb.append(" (giữ ")
                        .append(stock.getReserved())
                        .append(")");
            }

            sb.append("\n");
        }

        sb.append("\nVui lòng xử lý tồn kho trước khi xóa.");

        return sb.toString();
    }
}
