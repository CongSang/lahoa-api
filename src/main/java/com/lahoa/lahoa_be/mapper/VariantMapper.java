package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductPropertyResponseDTO;
import com.lahoa.lahoa_be.dto.response.VariantResponseDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VariantMapper {

    public VariantResponseDTO toDTO(ProductVariantEntity variant) {
        return VariantResponseDTO.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .stock(0)
                .status(variant.getStatus())
                .isDefault(variant.isDefault())
                .values(
                        variant.getPropertyValues().stream()
                                .map(vpv -> {
                                    var val = vpv.getPropertyValue();
                                    return DropdownResponseDTO.builder()
                                            .id(val.getId())
                                            .value(val.getValue())
                                            .label(val.getLabel())
                                            .build();
                                })
                                .toList()
                )
                .build();
    }
}
