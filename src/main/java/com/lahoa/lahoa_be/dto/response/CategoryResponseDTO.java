package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.Status;
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
    private Long productCount;
    private Status status;
    private List<CategoryResponseDTO> children;
}
