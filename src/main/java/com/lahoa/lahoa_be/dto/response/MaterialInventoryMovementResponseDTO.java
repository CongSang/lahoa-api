package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MaterialInventoryMovementResponseDTO {

    private Long id;
    private String code;

    private Long materialId;
    private String materialName;

    private Long warehouseId;
    private String warehouseName;

    private InventoryMovementType type;

    private Integer quantity;

    private Integer beforeOnHand;
    private Integer afterOnHand;

    private Integer beforeReserved;
    private Integer afterReserved;

    private String note;

    private Long actorId;
    private Long actorEmail;

    private InventoryReferenceType referenceType;

    private String referenceId;

    private LocalDateTime createdAt;
}
