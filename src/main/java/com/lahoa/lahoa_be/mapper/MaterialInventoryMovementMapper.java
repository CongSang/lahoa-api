package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.MaterialInventoryMovementResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryMovementEntity;
import org.springframework.stereotype.Component;

@Component
public class MaterialInventoryMovementMapper {

    public MaterialInventoryMovementResponseDTO toDTO(MaterialInventoryMovementEntity entity) {
        if (entity == null) return null;
        return MaterialInventoryMovementResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .materialId(entity.getMaterial().getId())
                .materialName(entity.getMaterial().getName())
                .warehouseId(entity.getWarehouse().getId())
                .warehouseName(entity.getWarehouse().getName())
                .type(entity.getType())
                .quantity(entity.getQuantity())
                .beforeOnHand(entity.getBeforeOnHand())
                .afterOnHand(entity.getAfterOnHand())
                .beforeReserved(entity.getBeforeReserved())
                .afterReserved(entity.getAfterReserved())
                .note(entity.getNote())
                .actorId(entity.getActorId())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
