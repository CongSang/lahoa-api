package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialWarehouseInventoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;

import java.util.List;

public interface MaterialInventoryService {

    // Nhập kho
    void importStock(InventoryActionRequestDTO req);

    // Điều chỉnh kho
    void adjustStock(InventoryActionRequestDTO req);

    // Để dành
    void reserve(InventoryActionRequestDTO req);

    // Giải phóng
    void release(InventoryActionRequestDTO req);

    // Tiêu thụ
    void consume(InventoryActionRequestDTO req);

    PagedResponseDTO<MaterialInventorySummaryResponseDTO> list(MaterialInventoryFilterRequestDTO filter);

    List<MaterialWarehouseInventoryResponseDTO> getWarehouseInventories(Long materialId);
}
