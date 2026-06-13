package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MaterialInventoryMovementFilterRequestDTO extends PagedRequestDTO {

    private String keyword;

    private Long materialId;

    private Long warehouseId;

    private InventoryMovementType type;

    private InventoryReferenceType referenceType;

    private String referenceId;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
