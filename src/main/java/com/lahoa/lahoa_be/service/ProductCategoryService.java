package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.CategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.response.*;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;

import java.util.List;

public interface ProductCategoryService {

    /**
     * Admin - Danh sách category phân trang
     */
    PagedResponseDTO<CategoryResponseDTO> list(
            CategoryFilterRequestDTO filter
    );

    /**
     * Admin - Danh sách category cha
     */
    List<DropdownResponseDTO> getCategoryParent();

    /**
     * Admin - Dropdown tree category
     */
    List<ProductPropertyResponseDTO> getDropdownCategory();

    /**
     * Ecommerce - Chi tiết category theo slug
     */
    CategoryEcResponseDTO getBySlug(String slug);

    /**
     * Ecommerce - Tree category
     */
    List<CategoryEcResponseDTO> getCategoryTree();

    /**
     * Tạo category
     */
    CategoryResponseDTO create(CategoryRequestDTO request);

    /**
     * Cập nhật category
     */
    CategoryResponseDTO update(Long id, CategoryRequestDTO request
    );

    /**
     * Soft delete category
     */
    void delete(Long id);

    /**
     * Restore category
     */
    CategoryResponseDTO restore(Long id);

    /**
     * Update status
     */
    void updateStatus(Long id, Status status);
}