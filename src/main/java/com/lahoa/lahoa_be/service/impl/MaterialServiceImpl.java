package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.CodePrefix;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.MaterialMapper;
import com.lahoa.lahoa_be.repository.MaterialCategoryRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.service.CloudinaryService;
import com.lahoa.lahoa_be.service.CodeGeneratorService;
import com.lahoa.lahoa_be.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialCategoryRepository categoryRepository;
    private final MaterialMapper materialMapper;
    private final CloudinaryService cloudinaryService;
    private final CodeGeneratorService codeService;

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

    @Override
    @Transactional
    public MaterialResponseDTO create(MaterialRequestDTO req) {
        MaterialCategoryEntity category =
                getCategory(req.getCategoryId());

        try {
            MaterialEntity entity = materialMapper.toEntity(req);

            entity.setCode(codeService.next(CodePrefix.MAT));
            entity.setCategory(category);

            MaterialEntity saved = materialRepository.save(entity);

            log.info("Created Material id={}", saved.getId());

            return materialMapper.toDTO(saved);
        } catch (Exception e) {
            if (req.getThumbnailPublicId() != null) {
                cloudinaryService.deleteImage(req.getThumbnailPublicId());
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public MaterialResponseDTO update(Long id, MaterialRequestDTO req) {
        MaterialEntity entity = getEntity(id);

        String oldPublicId = entity.getThumbnailPublicId();
        String newPublicId = req.getThumbnailPublicId();

        try {
            if (oldPublicId != null && !oldPublicId.equals(newPublicId)) {
                cloudinaryService.deleteAfterCommit(oldPublicId);
            }

            entity.setCategory(getCategory(req.getCategoryId()));
            entity.setName(req.getName());
            entity.setUnit(req.getUnit());
            entity.setThumbnail(req.getThumbnail());
            entity.setThumbnailPublicId(req.getThumbnailPublicId());
            entity.setLowStockThreshold(req.getLowStockThreshold());
            entity.setStatus(req.getStatus());

            log.info("Updated Material id={}", id);

            return materialMapper.toDTO(materialRepository.save(entity));
        } catch (Exception e) {
            if (newPublicId != null && !newPublicId.equals(oldPublicId)) {
                cloudinaryService.deleteImage(newPublicId);
            }
            throw e;
        }
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
