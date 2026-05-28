package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.MaterialUnit;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.util.BigDecimalPlainSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
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
}
