package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MaterialMapper {

    public MaterialEntity toEntity(MaterialRequestDTO dto) {
        if (dto == null) return null;
        return MaterialEntity.builder()
                .name(dto.getName())
                .unit(dto.getUnit())
                .thumbnail(dto.getThumbnail())
                .thumbnailPublicId(dto.getThumbnailPublicId())
                .lowStockThreshold(dto.getLowStockThreshold())
                .status(dto.getStatus())
                .build();
    }

    public MaterialResponseDTO toDTO(MaterialEntity entity) {
        if (entity == null) return null;
        return MaterialResponseDTO.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory().getId() )
                .categoryName(entity.getCategory().getName())
                .code(entity.getCode())
                .name(entity.getName())
                .unit(entity.getUnit())
                .thumbnail(entity.getThumbnail())
                .thumbnailPublicId(entity.getThumbnailPublicId())
                .lowStockThreshold(entity.getLowStockThreshold())
                .status(entity.getStatus())
                .build();
    }

    public DropdownResponseDTO toDropdown(MaterialEntity entity) {
        if (entity == null) return null;
        return DropdownResponseDTO.builder()
                .id(entity.getId())
                .value(entity.getId().toString())
                .label(entity.getName())
                .build();
    }
}
