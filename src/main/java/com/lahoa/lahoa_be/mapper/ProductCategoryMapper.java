package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.response.CategoryEcResponseDTO;
import com.lahoa.lahoa_be.dto.response.CategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductCategoryMapper {

    public ProductCategoryEntity toEntity(CategoryRequestDTO dto, String slug) {
        if (dto == null) return null;
        return ProductCategoryEntity.builder()
                .name(dto.getName().trim())
                .description(Optional.ofNullable(dto.getDescription()).orElse("").trim())
                .imageUrl(dto.getImageUrl())
                .imagePublicId(dto.getImagePublicId())
                .displayOrder(dto.getDisplayOrder())
                .slug(slug)
                .status(dto.getStatus())
                .seoTitle(dto.getSeoTitle())
                .seoDescription(dto.getSeoDescription())
                .seoKeywords(dto.getSeoKeywords())
                .build();
    }

    public CategoryResponseDTO toDTO(ProductCategoryEntity entity) {
        if (entity == null) return null;
        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .path(entity.getPath())
                .imageUrl(entity.getImageUrl())
                .imagePublicId(entity.getImagePublicId())
                .parent(this.toDTO(entity.getParent()))
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .seoTitle(entity.getSeoTitle())
                .seoDescription(entity.getSeoDescription())
                .seoKeywords(entity.getSeoKeywords())
                .build();
    }

    public CategoryEcResponseDTO toEcDTO(ProductCategoryEntity entity) {
        if (entity == null) return null;
        return CategoryEcResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public CategoryEcResponseDTO toTree(ProductCategoryEntity category, List<ProductCategoryEntity> allCategories) {
        return CategoryEcResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .imageUrl(category.getImageUrl())
                .children(allCategories.stream()
                        .filter(cat -> cat.getParent() != null && cat.getParent().getId().equals(category.getId()))
                        .map(child -> toTree(child, allCategories))
                        .collect(Collectors.toList()))
                .build();
    }

    public DropdownResponseDTO toDropdown(ProductCategoryEntity entity) {
        if (entity == null) return null;
        return DropdownResponseDTO.builder()
                .value(entity.getId())
                .label(entity.getName())
                .build();
    }
}
