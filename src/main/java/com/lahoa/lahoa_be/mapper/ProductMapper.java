package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.ProductResponseDTO;
import com.lahoa.lahoa_be.dto.response.VariantResponseDTO;
import com.lahoa.lahoa_be.entity.*;
import com.lahoa.lahoa_be.repository.ProductCategoryMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductCategoryMapper productCategoryMapper;
    private final ProductCategoryMappingRepository mappingRepository;
    private final PropertyMapper propertyMapper;
    private final VariantMapper variantMapper;

    public ProductEntity toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;
        return ProductEntity.builder()
                .name(dto.getName().trim())
                .description(
                        Optional.ofNullable(dto.getDescription()).orElse("").trim()
                )
                .basePrice(dto.getPrice())
                .displayOrder(dto.getDisplayOrder())
                .status(dto.getStatus())
                .mainImage(dto.getImageUrl())
                .imagePublicId(dto.getImagePublicId())
                .build();
    }

    public ProductResponseDTO toDTO(
            ProductEntity p,
            List<ProductVariantEntity> variants,
            List<ProductPropertyValueEntity> productPropertyValue
    ) {

        List<ProductCategoryMappingEntity> mappings = mappingRepository.findByProductId(p.getId());

        ProductCategoryMappingEntity primary = mappings.stream()
                .filter(ProductCategoryMappingEntity::getIsPrimary)
                .findFirst()
                .orElse(null);

        List<VariantResponseDTO> variantDTOs = variants.stream().map(
                variantMapper::toDTO
        ).toList();

        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .description(p.getDescription())
                .price(p.getBasePrice())
                .status(p.getStatus())
                .mainImage(p.getMainImage())
                .imagePublicId(p.getImagePublicId())
                .displayOrder(p.getDisplayOrder())
                .primaryCategory(primary != null
                        ? productCategoryMapper.toDTO(primary.getCategory())
                        : null)
                .categories(
                        mappings.stream()
                                .map(m -> productCategoryMapper.toDTO(m.getCategory()))
                                .toList()
                )
                .properties(propertyMapper.toProductPropertyDTO(productPropertyValue))
                .variants(variantDTOs)
                .seoTitle(p.getSeoTitle())
                .seoDescription(p.getSeoDescription())
                .seoKeywords(p.getSeoKeywords())
                .build();
    }

    public void apply(ProductEntity product, ProductRequestDTO req) {
        product.setName(req.getName().trim());
        product.setDescription(
                Optional.ofNullable(req.getDescription()).orElse("").trim()
        );
        product.setBasePrice(req.getPrice());
        product.setDisplayOrder(req.getDisplayOrder());
        product.setStatus(req.getStatus());
        product.setMainImage(req.getImageUrl());
        product.setImagePublicId(req.getImagePublicId());

        product.setSeoTitle(req.getSeoTitle());
        product.setSeoDescription(req.getSeoDescription());
        product.setSeoKeywords(req.getSeoKeywords());
    }
}
