package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.MaterialCategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialCategoryRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialCategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.MaterialCategoryMapper;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialCategoryRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.service.AuditLogService;
import com.lahoa.lahoa_be.service.MaterialCategoryService;
import com.lahoa.lahoa_be.specification.MaterialCategorySpecification;
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
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final MaterialCategoryMapper materialCategoryMapper;
    private final PagedMapper pagedMapper;
    private final AuditLogService auditService;

    private MaterialCategoryEntity getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException( "Không tìm thấy danh mục")
                );
    }

    private Long countMaterialByCategoryId(Long id) {
        return materialRepository.countByCategoryId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MaterialCategoryResponseDTO> list(MaterialCategoryFilterRequestDTO filter) {
        Specification<MaterialCategoryEntity> spec = MaterialCategorySpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<MaterialCategoryEntity> categoryPaged = categoryRepository.findAll(spec, pageable);
        List<Long> ids =
                categoryPaged.getContent()
                        .stream()
                        .map(
                                MaterialCategoryEntity::getId
                        )
                        .toList();

        Map<Long, Long> countMap =
                materialRepository
                        .countByCategoryIds(ids)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        row ->
                                                (Long) row[0],
                                        row ->
                                                (Long) row[1]
                                )
                        );

        List<MaterialCategoryResponseDTO> dtoList = categoryPaged.getContent()
                .stream().map(c -> materialCategoryMapper.toDTO(
                        c,
                        countMap.getOrDefault(c.getId(), 0L)
                )).toList();

        return pagedMapper.toDTO(categoryPaged, dtoList);
    }

    @Override
    public List<DropdownResponseDTO> getDropdown() {
        List<MaterialCategoryEntity> categories = categoryRepository.findAllByStatus(Status.ACTIVE);

        return categories.stream()
                .map(materialCategoryMapper::toDropdown)
                .toList();
    };

    @Override
    @Transactional
    public MaterialCategoryResponseDTO create(MaterialCategoryRequestDTO req) {

        MaterialCategoryEntity entity = materialCategoryMapper.toEntity(req);

        categoryRepository.save(entity);

        MaterialCategoryEntity saved = categoryRepository.save(entity);

        MaterialCategoryResponseDTO newCat = materialCategoryMapper.toDTO(saved, 0L);

        auditService.logAfterCommit(
                AuditAction.CREATE,
                AuditEntityType.MATERIAL_CATEGORY,
                saved.getId(),
                saved.getName(),
                null,
                newCat,
                null
        );

        log.info("Created Material Category id={}", saved.getId());

        return newCat;
    }

    @Override
    @Transactional
    public MaterialCategoryResponseDTO update(Long id, MaterialCategoryRequestDTO req) {
        MaterialCategoryEntity entity = getEntity(id);
        Long countMaterial = countMaterialByCategoryId(id);

        MaterialCategoryResponseDTO oldMaterial = materialCategoryMapper.toDTO(entity, countMaterial);

        entity.setName(req.getName());
        entity.setDescription(req.getDescription());
        entity.setStatus(req.getStatus());

        MaterialCategoryEntity saved = categoryRepository.save(entity);

        MaterialCategoryResponseDTO newCat = materialCategoryMapper.toDTO(saved, countMaterial);

        Map<String, Object> changed =
                CompareUtils.diff(oldMaterial, newCat);

        if (!changed.isEmpty()) {
            auditService.logAfterCommit(
                    AuditAction.UPDATE,
                    AuditEntityType.MATERIAL_CATEGORY,
                    saved.getId(),
                    saved.getName(),
                    null,
                    null,
                    changed
            );
        }

        log.info("Updated Material Category id={}", id);

        return newCat;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MaterialCategoryEntity entity = getEntity(id);

        if (materialRepository.existsByCategoryId(id)) {
            throw new BadRequestException(
                    "Danh mục đang được sử dụng"
            );
        }

        entity.setStatus(Status.DELETED);

        auditService.logAfterCommit(
                AuditAction.DELETE,
                AuditEntityType.MATERIAL_CATEGORY,
                entity.getId(),
                entity.getName(),
                null,
                null,
                null
        );

        log.info("Soft deleted Material Category id={}", id);
    }

    @Override
    @Transactional
    public MaterialCategoryResponseDTO restore(Long id) {
        MaterialCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (category.getStatus() != Status.DELETED) {
            throw new BadRequestException("Danh mục chưa bị xóa");
        }

        category.setStatus(Status.ACTIVE);

        MaterialCategoryEntity saved = categoryRepository.save(category);

        auditService.logAfterCommit(
                AuditAction.RESTORE,
                AuditEntityType.MATERIAL_CATEGORY,
                saved.getId(),
                saved.getName(),
                null,
                null,
                null
        );

        log.info("Restored Material Category id={}", id);

        return materialCategoryMapper.toDTO(saved, 0L);
    }

    @Override
    @Transactional
    public MaterialCategoryResponseDTO updateStatus(Long id) {
        MaterialCategoryEntity entity = getEntity(id);
        Long countMaterial = countMaterialByCategoryId(id);

        MaterialCategoryResponseDTO oldMaterial = materialCategoryMapper.toDTO(entity, countMaterial);

        entity.setStatus(entity.getStatus()
                        == Status.ACTIVE
                        ? Status.INACTIVE
                        : Status.ACTIVE
        );

        MaterialCategoryEntity saved = categoryRepository.save(entity);

        MaterialCategoryResponseDTO newCat = materialCategoryMapper.toDTO(saved, countMaterial);

        Map<String, Object> changed =
                CompareUtils.diff(oldMaterial, newCat);

        if (!changed.isEmpty()) {
            auditService.logAfterCommit(
                    AuditAction.UPDATE,
                    AuditEntityType.MATERIAL_CATEGORY,
                    saved.getId(),
                    saved.getName(),
                    null,
                    null,
                    changed
            );
        }

        log.info("Changed status Material Category id={} to {}", id, newCat.getStatus());

        return newCat;
    }
}
