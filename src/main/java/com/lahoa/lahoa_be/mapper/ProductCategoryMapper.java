package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import com.lahoa.lahoa_be.dto.response.CategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.UserResponseDTO;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductCategoryMapper {

    public ProductCategoryEntity toEntity(CategoryRequestDTO dto, String slug) {
        if (dto == null) return null;
        return ProductCategoryEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .displayOrder(dto.getDisplayOrder())
                .slug(slug)
                .status(Status.ACTIVE)
                .build();
    }

    public CategoryResponseDTO toDTO(ProductCategoryEntity entity) {
        if (entity == null) return null;
        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .imageUrl(entity.getImageUrl())
                .displayOrder(entity.getDisplayOrder())
                .children(entity.getChildren().stream().map(this::toDTO).collect(Collectors.toList()))
                .build();
    }

    public CategoryResponseDTO toDTONoChild(ProductCategoryEntity entity) {
        if (entity == null) return null;
        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public CategoryResponseDTO toTree(ProductCategoryEntity category, List<ProductCategoryEntity> allCategories) {
        return CategoryResponseDTO.builder()
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
}
