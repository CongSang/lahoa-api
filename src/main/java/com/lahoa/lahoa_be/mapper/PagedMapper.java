package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedMapper {

    public <T> PagedResponseDTO<T> toDTO(Page<?> page, List<T> data) {
        return PagedResponseDTO.<T>builder()
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(data)
                .isLast(page.isLast())
                .build();
    }
}
