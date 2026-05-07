package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.ProductFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductResponseDTO;

public interface ProductService {

    /**
     * Danh sách sản phẩm phân trang
     */
    PagedResponseDTO<ProductResponseDTO> list(
            ProductFilterRequestDTO filter
    );

    /**
     * Tạo sản phẩm
     */
    ProductResponseDTO create(
            ProductRequestDTO req
    );

    /**
     * Cập nhật sản phẩm
     */
    ProductResponseDTO update(
            Long id,
            ProductRequestDTO req
    );

    /**
     * Lấy sản phẩm theo slug
     */
    ProductResponseDTO getBySlug(
            String slug
    );

    /**
     * Soft delete sản phẩm
     */
    void delete(
            Long id
    );
}