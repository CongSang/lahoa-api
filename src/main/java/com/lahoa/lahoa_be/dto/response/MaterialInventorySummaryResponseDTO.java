package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.MaterialUnit;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.util.BigDecimalPlainSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialInventorySummaryResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    private String categoryName;

    private String code;
    private String name;

    private MaterialUnit unit;

    private String thumbnail;
    private String thumbnailPublicId;

    private Status status;

    private Integer warehouseCount;

    private Integer onHand;

    private Integer reserved;

    private Integer available;

    private Integer lowStockThreshold;

    private Boolean hasLowStockWarehouse;

    private Boolean hasOutOfStockWarehouse;

    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal costPrice;

    public MaterialInventorySummaryResponseDTO(
            Long id,
            Long categoryId,
            String categoryName,
            String code,
            String name,
            MaterialUnit unit,
            String thumbnail,
            String thumbnailPublicId,
            Status status,
            Long warehouseCount,
            Long onHand,
            Long reserved,
            Long available,
            Long lowStockThreshold,
            Boolean hasLowStockWarehouse,
            Boolean hasOutOfStockWarehouse,
            Double costPrice
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.thumbnail = thumbnail;
        this.thumbnailPublicId = thumbnailPublicId;
        this.status = status;

        this.warehouseCount = warehouseCount != null ? warehouseCount.intValue() : 0;
        this.onHand = onHand != null ? onHand.intValue() : 0;
        this.reserved = reserved != null ? reserved.intValue() : 0;
        this.available = available != null ? available.intValue() : 0;
        this.lowStockThreshold = lowStockThreshold != null ? lowStockThreshold.intValue() : 0;
        this.hasLowStockWarehouse = hasLowStockWarehouse != null ? hasLowStockWarehouse : false;
        this.hasOutOfStockWarehouse = hasOutOfStockWarehouse != null ? hasOutOfStockWarehouse : false;
        this.costPrice = costPrice != null ? BigDecimal.valueOf(costPrice) : BigDecimal.ZERO;
    }
}
