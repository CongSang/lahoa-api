package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.MaterialUnit;
import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StocktakeDetailResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialId;

    private String materialCode;

    private String materialName;

    private Status materialStatus;

    private MaterialUnit unit;

    private Integer systemQty;

    private Integer actualQty;

    private Integer difference;
}
