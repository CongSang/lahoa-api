package com.lahoa.lahoa_be.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MaterialInventoryResponseDTO {

    private Long id;

    private MaterialResponseDTO material;

    private Long warehouseId;
    private String warehouseName;

    private Integer onHand;

    private Integer reserved; // Đang giữ cho khách

    private Integer available;

    private Integer lowStockThreshold;

    private Boolean lowStock;
}
