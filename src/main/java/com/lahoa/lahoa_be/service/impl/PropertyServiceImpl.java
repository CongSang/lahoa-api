package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductPropertyResponseDTO;
import com.lahoa.lahoa_be.entity.*;
import com.lahoa.lahoa_be.repository.ProductPropertyValueRepository;
import com.lahoa.lahoa_be.repository.PropertyRepository;
import com.lahoa.lahoa_be.repository.PropertyValueRepository;
import com.lahoa.lahoa_be.repository.VariantPropertyValueRepository;
import com.lahoa.lahoa_be.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;
    private final ProductPropertyValueRepository productPropertyValueRepository;
    private final VariantPropertyValueRepository variantPropertyValueRepository;

    @Override
    public List<ProductPropertyResponseDTO> getProperties(boolean isFilterable) {
        List<PropertyEntity> properties = propertyRepository.findByFilterable(isFilterable);

        return properties.stream()
                .map(p -> ProductPropertyResponseDTO.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .name(p.getName())
                        .values(
                                p.getValues().stream()
                                        .sorted(Comparator.comparing(
                                                PropertyValueEntity::getId
                                        ))
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

    @Override
    @Transactional
    public void syncProductProperties(ProductEntity product, ProductRequestDTO req) {

        List<Long> newIds = Optional.ofNullable(req.getPropertyValueIds())
                .orElse(List.of());

        product.getPropertyValues().clear();
        productPropertyValueRepository.flush();

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

    @Override
    @Transactional
    public void syncVariantProperties(ProductVariantEntity variant, List<Long> valueIds) {

        variant.getPropertyValues().clear();
        variantPropertyValueRepository.flush();

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
