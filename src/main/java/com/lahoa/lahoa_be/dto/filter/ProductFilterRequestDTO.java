package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductFilterRequestDTO extends PagedRequestDTO {
    private String keyword;

    private Long categoryId;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private ProductStatus status;

    private Map<Long, List<Long>> propertyValueIds;
//    private Map<Long, List<Long>> variantPropertyValueIds;
}
