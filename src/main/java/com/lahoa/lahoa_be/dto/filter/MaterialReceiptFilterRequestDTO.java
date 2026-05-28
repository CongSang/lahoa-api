package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MaterialReceiptFilterRequestDTO extends PagedRequestDTO {

    private String keyword;

    private Long warehouseId;
}
