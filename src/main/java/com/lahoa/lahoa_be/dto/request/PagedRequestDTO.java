package com.lahoa.lahoa_be.dto.request;

import lombok.Data;

@Data
public class PagedRequestDTO {

    private int page = 0;
    private int size = 10;
    private String sortField = "id";
    private String sortOrder = "desc";
}
