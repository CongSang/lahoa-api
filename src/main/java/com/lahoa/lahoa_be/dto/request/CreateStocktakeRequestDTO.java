package com.lahoa.lahoa_be.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateStocktakeRequestDTO {

    @NotNull
    private Long warehouseId;

    private String note;

    @NotEmpty
    private List<CreateStocktakeDetailRequestDTO> details;
}
