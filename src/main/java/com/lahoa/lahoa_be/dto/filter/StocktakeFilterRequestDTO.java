package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class StocktakeFilterRequestDTO extends PagedRequestDTO {

    private String keyword;

    private Long warehouseId;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
