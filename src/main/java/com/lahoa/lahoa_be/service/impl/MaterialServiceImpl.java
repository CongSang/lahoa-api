package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.MaterialFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.MaterialMapper;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialCategoryRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.service.CloudinaryService;
import com.lahoa.lahoa_be.service.MaterialService;
import com.lahoa.lahoa_be.specification.MaterialSpecification;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialCategoryRepository categoryRepository;
    private final MaterialMapper materialMapper;
    private final PagedMapper pagedMapper;
    private final CloudinaryService cloudinaryService;

    private MaterialCategoryEntity getCategory( Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy danh mục"
                        )
                );
    }

    private MaterialEntity getEntity(Long id) {
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy nguyên liệu"
                        )
                );

        if (material.getStatus() == Status.DELETED) {
            throw new BadRequestException("Nguyên liệu đã bị xóa");
        }

        return material;
    }

    private String generateUniqueCode(String base, Long excludeId) {
        String code = base;
        int i = 1;

        while (materialRepository.existsByCodeAndIdNot(code, excludeId)) {
            code = base + "-" + i++;
        }

        return code;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MaterialResponseDTO> list(MaterialFilterRequestDTO filter) {
        Specification<MaterialEntity> spec = MaterialSpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<MaterialEntity> categoryPaged = materialRepository.findAll(spec, pageable);

        List<MaterialResponseDTO> dtoList = categoryPaged.getContent()
                .stream().map(materialMapper::toDTO).toList();

        return pagedMapper.toDTO(categoryPaged, dtoList);
    }

    @Override
    public MaterialResponseDTO getById(Long id) {
        return materialMapper.toDTO(getEntity(id));
    }

    @Override
    @Transactional
    public MaterialResponseDTO create(MaterialRequestDTO req) {
        MaterialCategoryEntity category =
                getCategory(req.getCategoryId());

        MaterialEntity entity = materialMapper.toEntity(req);

        String code = generateUniqueCode(
                SlugUtils.generateSlug(req.getName()).toUpperCase(),
                null
        );

        entity.setCode(code);
        entity.setCategory(category);

        materialRepository.save(entity);

        return materialMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public MaterialResponseDTO update(Long id, MaterialRequestDTO req) {
        MaterialEntity entity = getEntity(id);

        String code = generateUniqueCode(
                SlugUtils.generateSlug(req.getName()).toUpperCase(),
                id
        );

        entity.setCode(code);
        entity.setCategory(getCategory(req.getCategoryId()));
        entity.setName(req.getName());
        entity.setUnit(req.getUnit());
        entity.setThumbnail(req.getThumbnail());
        entity.setDefaultCost(req.getDefaultCost());
        entity.setLowStockThreshold(req.getLowStockThreshold());
        entity.setStatus(req.getStatus());

        return materialMapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MaterialEntity material = getEntity(id);

        cloudinaryService.deleteAfterCommit(material.getThumbnailPublicId());
        material.setThumbnail(null);
        material.setThumbnailPublicId(null);
        material.setStatus(Status.DELETED);

        log.info("Soft deleted Material id={}", id);
    }

    @Override
    @Transactional
    public MaterialResponseDTO restore(Long id) {
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu"));

        if (material.getStatus() != Status.DELETED) {
            throw new BadRequestException("Nguyên liệu chưa bị xóa");
        }

        String code = generateUniqueCode(
                SlugUtils.generateSlug(material.getName()).toUpperCase(),
                id
        );

        material.setCode(code);
        material.setStatus(Status.ACTIVE);

        MaterialEntity saved = materialRepository.save(material);

        log.info("Restored Material id={}", id);

        return materialMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public MaterialResponseDTO updateStatus(Long id) {
        MaterialEntity entity = getEntity(id);

        entity.setStatus(entity.getStatus()
                == Status.ACTIVE
                ? Status.INACTIVE
                : Status.ACTIVE
        );

        return materialMapper.toDTO(entity);
    }
}
