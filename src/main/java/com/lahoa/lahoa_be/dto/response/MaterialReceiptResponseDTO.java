package com.lahoa.lahoa_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class MaterialReceiptResponseDTO {

    private Long id;

    private String code;

    private String supplier;

    private String note;

    private String warehouseName;

    private BigDecimal totalCost;

    private List<MaterialReceiptDetailResponseDTO> details;

    private LocalDateTime createdAt;
}
