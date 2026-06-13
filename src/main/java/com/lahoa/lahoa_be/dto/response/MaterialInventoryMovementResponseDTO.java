package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MaterialInventoryMovementResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String code;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialId;
    private String materialName;
    private Status materialStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long warehouseId;
    private String warehouseName;
    private Status warehouseStatus;

    private InventoryMovementType type;

    private Integer quantity;

    private Integer beforeOnHand;
    private Integer afterOnHand;

    private Integer beforeReserved;
    private Integer afterReserved;

    private String note;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long actorId;
    private String actorName;
    private String actorEmail;

    private InventoryReferenceType referenceType;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long referenceId;
    private String referenceCode;

    private LocalDateTime createdAt;
}
