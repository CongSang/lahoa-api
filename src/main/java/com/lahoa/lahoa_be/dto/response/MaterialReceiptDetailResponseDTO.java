package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.util.BigDecimalPlainSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class MaterialReceiptDetailResponseDTO {

    private String materialName;
    private Status materialStatus;

    private Integer quantity;

    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal unitCost;

    private BigDecimal subtotal;
}
