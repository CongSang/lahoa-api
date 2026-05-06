package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.VariantStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class VariantResponseDTO {
    private Long id;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private VariantStatus status;
    private boolean isDefault;
    boolean hasRecipe;

    private List<DropdownResponseDTO> values;
}
