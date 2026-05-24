package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.request.MaterialCategoryRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialCategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class MaterialCategoryMapper {

    public MaterialCategoryEntity toEntity(MaterialCategoryRequestDTO dto) {
        if (dto == null) return null;
        return MaterialCategoryEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();
    }

    public MaterialCategoryResponseDTO toDTO(MaterialCategoryEntity entity, Long materialCount) {
        if (entity == null) return null;
        return MaterialCategoryResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .materialCount(materialCount)
                .build();
    }

    public DropdownResponseDTO toDropdown(MaterialCategoryEntity entity) {
        if (entity == null) return null;
        return DropdownResponseDTO.builder()
                .id(entity.getId())
                .value(entity.getId().toString())
                .label(entity.getName())
                .build();
    }
}
