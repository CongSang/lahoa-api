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
public class CategoryEcResponseDTO {

    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private List<CategoryEcResponseDTO> children;
}
