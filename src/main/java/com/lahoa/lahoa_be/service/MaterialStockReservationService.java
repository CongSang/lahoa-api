package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryResponseDTO;

public interface MaterialStockReservationService {

    // Để dành
    MaterialInventoryResponseDTO reserve(InventoryActionRequestDTO req);

    // Giải phóng
    MaterialInventoryResponseDTO release(InventoryActionRequestDTO req);

    // Tiêu thụ
    MaterialInventoryResponseDTO consume(InventoryActionRequestDTO req);
}
