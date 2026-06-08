package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StocktakeResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String code;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long warehouseId;
    private String warehouseName;
    private Status warehouseStatus;

    private String note;

    private Integer totalItems;

    private Integer totalDifference;

    private String createdBy;
    private LocalDateTime createdAt;

    private List<StocktakeDetailResponseDTO> details;
}
