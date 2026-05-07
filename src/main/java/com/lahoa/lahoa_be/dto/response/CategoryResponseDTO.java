package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String slug;
    private String path;
    private String description;
    private String imageUrl;
    private String imagePublicId;
    private Integer displayOrder;
    private Long productCount;
    private Status status;

    private CategoryResponseDTO parent;
    private List<CategoryResponseDTO> children;

    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
}
