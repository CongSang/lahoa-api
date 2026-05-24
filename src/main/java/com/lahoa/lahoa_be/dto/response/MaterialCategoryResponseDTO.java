package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MaterialCategoryResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String code;
    private String name;
    private String description;
    private Status status;

    private Long materialCount;
}