package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryFilter extends PagedRequestDTO {

    private String keyword;
    private Status status;
    private Long parentId;
}
