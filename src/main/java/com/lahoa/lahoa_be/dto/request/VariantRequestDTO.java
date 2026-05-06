package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.VariantStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VariantRequestDTO {
    private Long id;

    private String sku;
    private BigDecimal price;
    private boolean isDefault;

    private List<Long> propertyValueIds;
}
