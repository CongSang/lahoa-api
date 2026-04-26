package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.entity.ProductCategoryId;
import com.lahoa.lahoa_be.entity.ProductCategoryMappingEntity;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.repository.ProductCategoryMappingRepository;
import com.lahoa.lahoa_be.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryMappingService {

    private final ProductCategoryMappingRepository mappingRepository;
    private final ProductCategoryRepository categoryRepository;

    @Transactional
    public void syncCategories(ProductEntity product, ProductRequestDTO req) {

        List<ProductCategoryMappingEntity> existing =
                mappingRepository.findByProductId(product.getId());

        Map<Long, ProductCategoryMappingEntity> existingMap =
                existing.stream().collect(Collectors.toMap(
                        m -> m.getCategory().getId(),
                        m -> m
                ));

        List<ProductCategoryMappingEntity> newMappings = new ArrayList<>();

        for (Long categoryId : req.getCategoryIds()) {

            ProductCategoryMappingEntity mapping = existingMap.get(categoryId);

            if (mapping == null) {
                mapping = new ProductCategoryMappingEntity();

                mapping.setId(new ProductCategoryId(
                        product.getId(), categoryId
                ));
                mapping.setProduct(product);
                mapping.setCategory(
                        categoryRepository.getReferenceById(categoryId)
                );
            }

            mapping.setIsPrimary(
                    categoryId.equals(req.getPrimaryCategoryId())
            );

            newMappings.add(mapping);
        }

        // remove old
        List<ProductCategoryMappingEntity> toRemove = existing.stream()
                .filter(m -> !req.getCategoryIds()
                        .contains(m.getCategory().getId()))
                .toList();

        mappingRepository.deleteAll(toRemove);
        mappingRepository.saveAll(newMappings);
    }
}
