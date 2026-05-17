package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.VariantStatus;
import com.lahoa.lahoa_be.util.BigDecimalPlainSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class VariantResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String sku;

    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal price;
    private Integer stock;
    private VariantStatus status;
    private boolean isDefault;
    boolean hasRecipe;

    private List<ProductPropertyResponseDTO> properties;
}
