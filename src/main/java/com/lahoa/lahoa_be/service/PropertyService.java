package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.ProductPropertyResponseDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;

import java.util.List;

public interface PropertyService {

    List<ProductPropertyResponseDTO> getProperties(boolean isFilterable);

    void syncProductProperties(ProductEntity product, ProductRequestDTO req);

    void syncVariantProperties(ProductVariantEntity variant, List<Long> valueIds);
}
