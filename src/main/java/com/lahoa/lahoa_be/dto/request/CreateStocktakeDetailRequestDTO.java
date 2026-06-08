package com.lahoa.lahoa_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStocktakeDetailRequestDTO {

    @NotNull
    private Long materialId;

    @NotNull
    private Integer actualQty;
}
