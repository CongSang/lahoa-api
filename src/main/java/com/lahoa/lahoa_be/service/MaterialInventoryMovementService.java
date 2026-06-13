package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import com.lahoa.lahoa_be.dto.filter.MaterialInventoryMovementFilterRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryMovementResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;

public interface MaterialInventoryMovementService {

    void log(
            MaterialInventoryEntity inv,
            InventoryMovementType type,
            Integer quantity,
            Integer beforeOnHand,
            Integer beforeReserved,
            String note,
            InventoryReferenceType referenceType,
            Long referenceId,
            String referenceCode
    );

    PagedResponseDTO<MaterialInventoryMovementResponseDTO> list(MaterialInventoryMovementFilterRequestDTO filter);
}
