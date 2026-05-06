package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private String mainImage;
    private String imagePublicId;
    private Integer displayOrder;
    private ProductStatus status;

    private CategoryResponseDTO primaryCategory;
    private List<CategoryResponseDTO> categories;

    private List<ProductPropertyResponseDTO> properties;

    private List<VariantResponseDTO> variants;

    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
}
