package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;

public interface ProductCategoryMappingService {

    void syncCategories(ProductEntity product, ProductRequestDTO req);
}
