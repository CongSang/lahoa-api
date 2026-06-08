package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.MaterialUnit;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakeMaterialResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialId;

    private String materialCode;

    private String materialName;

    private MaterialUnit unit;

    private Integer systemQty;

    private Integer actualQty;
}
