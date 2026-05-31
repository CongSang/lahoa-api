package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.MaterialInventoryResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialInventoryMapper {

    private final MaterialMapper materialMapper;

    public MaterialInventoryResponseDTO toDTO(MaterialInventoryEntity entity) {
        if (entity == null) return null;

        int available = entity.getAvailable();

        int threshold = entity.getMaterial().getLowStockThreshold();

        return MaterialInventoryResponseDTO.builder()
                .id(entity.getId())
                .material(materialMapper.toDTO(entity.getMaterial()))
                .warehouseId(entity.getWarehouse().getId())
                .warehouseName(entity.getWarehouse().getName())
                .onHand(entity.getOnHand())
                .reserved(entity.getReserved())
                .available(available)
                .lowStockThreshold(threshold)
                .lowStock(available <= threshold)
                .build();
    }
}
