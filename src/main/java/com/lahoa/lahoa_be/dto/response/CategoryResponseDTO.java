package com.lahoa.lahoa_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private Integer displayOrder;
    private List<CategoryResponseDTO> children;
}
