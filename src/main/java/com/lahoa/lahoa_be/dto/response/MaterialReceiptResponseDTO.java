package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.util.BigDecimalPlainSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class MaterialReceiptResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String code;

    private String supplier;

    private String note;

    private String warehouseName;
    private Status warehouseStatus;

    @JsonSerialize(using = BigDecimalPlainSerializer.class)
    private BigDecimal totalCost;

    private Integer itemCount;
    private List<MaterialReceiptDetailResponseDTO> details;

    private LocalDateTime createdAt;
}
