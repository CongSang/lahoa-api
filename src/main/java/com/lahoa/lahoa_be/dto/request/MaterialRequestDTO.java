package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.MaterialUnit;
import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialRequestDTO {

    private Long categoryId;

    private String code;
    private String name;

    private MaterialUnit unit;

    private String thumbnail;
    private String thumbnailPublicId;

    private BigDecimal defaultCost;

    private Integer lowStockThreshold;

    private Status status;
}
