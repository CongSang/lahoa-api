package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.util.BigDecimalPlainSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class MaterialWarehouseInventoryResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long warehouseId;

    private String warehouseName;

    private Integer onHand;

    private Integer reserved;

    private Integer available;

    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal costPrice;

    private Boolean lowStock;

    private Boolean outOfStock;

    private LocalDateTime updatedAt;
}
