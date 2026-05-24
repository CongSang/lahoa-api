package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.MaterialCategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.filter.WarehouseFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.WarehouseRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialCategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.WarehouseResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.entity.WarehouseEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.WarehouseMapper;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.WarehouseService;
import com.lahoa.lahoa_be.specification.MaterialCategorySpecification;
import com.lahoa.lahoa_be.specification.WarehouseSpecification;
import com.lahoa.lahoa_be.util.SlugUtils;
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
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final PagedMapper pagedMapper;

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
        List<Long> ids =
                whPaged.getContent()
                        .stream()
                        .map(
                                WarehouseEntity::getId
                        )
                        .toList();

        List<WarehouseResponseDTO> dtoList = whPaged.getContent()
                .stream().map(warehouseMapper::toDTO).toList();

        return pagedMapper.toDTO(whPaged, dtoList);
    }

    @Override
    @Transactional
    public WarehouseResponseDTO create(WarehouseRequestDTO req) {
        validateCode(req.getCode(), null);

        WarehouseEntity entity = warehouseMapper.toEntity(req);

        warehouseRepository.save(entity);

        return warehouseMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public WarehouseResponseDTO update(Long id, WarehouseRequestDTO req) {
        WarehouseEntity entity = getEntity(id);

        validateCode(req.getCode(), id);

        entity.setCode(req.getCode());
        entity.setName(req.getName());
        entity.setAddress(req.getAddress());
        entity.setStatus(req.getStatus());

        return warehouseMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public WarehouseResponseDTO updateStatus(Long id) {
        WarehouseEntity entity = getEntity(id);

        entity.setStatus(
                entity.getStatus() ==
                        Status.ACTIVE
                        ? Status.INACTIVE
                        : Status.ACTIVE
        );

        return warehouseMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        WarehouseEntity entity = getEntity(id);

        entity.setStatus(Status.DELETED);

        log.info("Soft deleted Warehouse id={}", id);
    }

    @Override
    @Transactional
    public WarehouseResponseDTO restore(Long id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

        if (warehouse.getStatus() != Status.DELETED) {
            throw new BadRequestException("Kho chưa bị xóa");
        }

        warehouse.setStatus(Status.ACTIVE);

        WarehouseEntity saved = warehouseRepository.save(warehouse);

        log.info("Restored Warehouse id={}", id);

        return warehouseMapper.toDTO(saved);
    }
}
