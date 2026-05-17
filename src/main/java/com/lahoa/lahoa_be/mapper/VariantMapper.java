package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.VariantResponseDTO;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VariantMapper {
    private final PropertyMapper propertyMapper;

    public VariantResponseDTO toDTO(ProductVariantEntity variant) {
        return VariantResponseDTO.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .stock(0)
                .status(variant.getStatus())
                .isDefault(variant.isDefault())
                .properties(propertyMapper.toVariantPropertyDTO(variant.getPropertyValues()))
                .build();
    }
}
