package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;

public interface MaterialInventoryService {

    // Nhập kho
    void importStock(InventoryActionRequestDTO req);

    // Điều chỉnh kho
    void adjustStock(InventoryActionRequestDTO req);

    PagedResponseDTO<MaterialInventorySummaryResponseDTO> list(MaterialInventoryFilterRequestDTO filter);
}
