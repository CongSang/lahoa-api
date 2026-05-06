package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.VariantStatus;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.request.VariantRequestDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;
import com.lahoa.lahoa_be.entity.PropertyValueEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.repository.PropertyValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantService {

    private final PropertyService propertyService;
    private final PropertyValueRepository propertyValueRepository;

    @Transactional
    public void syncVariants(ProductEntity product, ProductRequestDTO req) {

        List<VariantRequestDTO> incoming = Optional.ofNullable(req.getVariants())
                .orElse(List.of());

        // map existing
        Map<Long, ProductVariantEntity> existingMap =
                product.getVariants().stream()
                        .collect(Collectors.toMap(ProductVariantEntity::getId, v -> v));

        Set<Long> incomingIds = incoming.stream()
                .map(VariantRequestDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // DELETE
        for (ProductVariantEntity v : product.getVariants()) {
            if (v.getId() != null && !incomingIds.contains(v.getId())) {
                v.setStatus(VariantStatus.INACTIVE);
            }
        }

        // CREATE / UPDATE
        for (VariantRequestDTO dto : incoming) {

            ProductVariantEntity variant;

            if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
                // UPDATE
                variant = existingMap.get(dto.getId());
            } else {
                // CREATE
                variant = new ProductVariantEntity();
                variant.setStatus(VariantStatus.ACTIVE);
                variant.setProduct(product);
                product.getVariants().add(variant);
            }

            variant.setSku(resolveSku(product, variant, dto));
            variant.setPrice(dto.getPrice());
            variant.setDefault(dto.isDefault());

            propertyService.syncVariantProperties(variant, dto.getPropertyValueIds());
        }

        // VALIDATE DUPLICATE COMBINATION
        validateVariantCombination(product.getVariants());
        validateSku(product.getVariants());
    }

    private void validateVariantCombination(List<ProductVariantEntity> variants) {

        Set<String> seen = new HashSet<>();

        for (ProductVariantEntity v : variants) {

            String key = v.getPropertyValues().stream()
                    .map(vpv -> vpv.getPropertyValue().getId().toString())
                    .sorted()
                    .collect(Collectors.joining("-"));

            if (!seen.add(key)) {
                throw new BadRequestException("Biến thể bị trùng");
            }
        }
    }

    private void validateSku(List<ProductVariantEntity> variants) {

        Set<String> seen = new HashSet<>();

        for (ProductVariantEntity v : variants) {

            if (v.getStatus() == VariantStatus.INACTIVE) continue;

            if (v.getSku() == null || v.getSku().isBlank()) {
                throw new BadRequestException("SKU không được để trống");
            }

            if (!seen.add(v.getSku())) {
                throw new BadRequestException("SKU bị trùng: " + v.getSku());
            }
        }
    }

    private String generateSku(ProductEntity product, List<Long> valueIds) {

        String base = product.getSlug().toUpperCase();

        if (valueIds == null || valueIds.isEmpty()) {
            return base + "-DEFAULT";
        }

        List<PropertyValueEntity> values =
                propertyValueRepository.findAllWithPropertyById(valueIds);

        String attrs = values.stream()
                .sorted(Comparator.comparing(v -> v.getProperty().getCode()))
                .map(v -> {
                    String propCode = v.getProperty().getCode().toUpperCase();
                    String valCode = v.getValue().toUpperCase();
                    return propCode + "-" + valCode;
                })
                .collect(Collectors.joining("-"));

        String random = UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();

        return base + "-" + attrs + "-" + random;
    }

    private String resolveSku(
            ProductEntity product,
            ProductVariantEntity variant,
            VariantRequestDTO dto
    ) {
        if (variant.getSku() != null) {
            return variant.getSku();
        }

        return generateSku(product, dto.getPropertyValueIds());
    }
}
