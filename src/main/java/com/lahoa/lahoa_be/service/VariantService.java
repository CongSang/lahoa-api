package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.VariantStatus;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;

public interface VariantService {

    void syncVariants(ProductEntity product, ProductRequestDTO req);
}
