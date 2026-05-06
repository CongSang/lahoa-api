package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductPropertyResponseDTO;
import com.lahoa.lahoa_be.entity.*;
import com.lahoa.lahoa_be.repository.PropertyRepository;
import com.lahoa.lahoa_be.repository.PropertyValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;

    public List<ProductPropertyResponseDTO> getProperties(boolean isFilterable) {
        List<PropertyEntity> properties = propertyRepository.findByFilterable(isFilterable);

        return properties.stream()
                .map(p -> ProductPropertyResponseDTO.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .name(p.getName())
                        .values(
                                p.getValues().stream()
                                        .map(v -> DropdownResponseDTO.builder()
                                                .id(v.getId())
                                                .value(v.getValue())
                                                .label(v.getLabel())
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
                )
                .toList();
    }

    @Transactional
    public void syncProductProperties(ProductEntity product, ProductRequestDTO req) {

        List<Long> newIds = Optional.ofNullable(req.getPropertyValueIds())
                .orElse(List.of());

        // clear old
        product.getPropertyValues().clear();

        if (newIds.isEmpty()) return;

        List<PropertyValueEntity> values = propertyValueRepository.findAllById(newIds);

        List<ProductPropertyValueEntity> newList = values.stream()
                .map(val -> ProductPropertyValueEntity.builder()
                        .product(product)
                        .propertyValue(val)
                        .build()
                )
                .toList();

        product.getPropertyValues().addAll(newList);
    }

    public void syncVariantProperties(ProductVariantEntity variant, List<Long> valueIds) {

        variant.getPropertyValues().clear();

        if (valueIds == null || valueIds.isEmpty()) return;

        List<PropertyValueEntity> values = propertyValueRepository.findAllById(valueIds);

        List<VariantPropertyValueEntity> newList = values.stream()
                .map(val -> VariantPropertyValueEntity.builder()
                        .variant(variant)
                        .propertyValue(val)
                        .build()
                )
                .toList();

        variant.getPropertyValues().addAll(newList);
    }
}
