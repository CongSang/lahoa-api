package com.lahoa.lahoa_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class MaterialReceiptDetailResponseDTO {

    private String materialName;

    private Integer quantity;

    private BigDecimal unitCost;

    private BigDecimal subtotal;
}
