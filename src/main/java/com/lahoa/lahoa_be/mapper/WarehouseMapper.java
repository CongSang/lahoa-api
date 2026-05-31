package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.request.WarehouseRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.WarehouseResponseDTO;
import com.lahoa.lahoa_be.entity.WarehouseEntity;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseEntity toEntity(WarehouseRequestDTO dto) {
        if (dto == null) return null;
        return WarehouseEntity.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .status(dto.getStatus())
                .build();
    }

    public WarehouseResponseDTO toDTO(WarehouseEntity entity) {
        if (entity == null) return null;
        return WarehouseResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .materialCount(0L)
                .build();
    }

    public DropdownResponseDTO toDropdown(WarehouseEntity entity) {
        if (entity == null) return null;
        return DropdownResponseDTO.builder()
                .id(entity.getId())
                .value(entity.getId().toString())
                .label(entity.getName())
                .build();
    }
}
