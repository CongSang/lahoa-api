package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class InventoryActionRequestDTO {

    private Long materialId;

    private Long warehouseId;

    private Integer quantity;

    private BigDecimal unitCost;

    private String note;

    private InventoryReferenceType referenceType;

    private Long referenceId;

    private String referenceCode;
}
