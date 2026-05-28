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
public class MaterialResponseDTO {

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

    private Integer lowStockThreshold;

    private Status status;
}
