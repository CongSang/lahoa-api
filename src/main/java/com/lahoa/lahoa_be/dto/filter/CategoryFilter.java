package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Data;

@Data
public class CategoryFilter {

    private String keyword;
    private Status status;
    private Long parentId;
}
