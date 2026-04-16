package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.CategoryFilter;
import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import com.lahoa.lahoa_be.dto.response.CategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.ProductCategoryMapper;
import com.lahoa.lahoa_be.repository.ProductCategoryRepository;
import com.lahoa.lahoa_be.util.SlugUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper categoryMapper;
    private final PagedMapper pagedMapper;

    // Admin: Lấy danh sách
    public PagedResponseDTO<CategoryResponseDTO> getCategories(
            PagedRequestDTO pagedRequest,
            CategoryFilter filter
    ) {
        Sort sort = pagedRequest.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(pagedRequest.getSortField()).ascending()
                : Sort.by(pagedRequest.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                pagedRequest.getPage(),
                pagedRequest.getSize(),
                sort
        );

        Page<ProductCategoryEntity> categoriesPaged = categoryRepository
                .findByFilters(filter.getKeyword(), filter.getStatus(), filter.getParentId(), pageable);

        List<CategoryResponseDTO> content = categoriesPaged.getContent().stream()
                .map(categoryMapper::toDTONoChild)
                .collect(Collectors.toList());

        return pagedMapper.toDTO(categoriesPaged, content);
    }

    // Admin: Lấy tất cả danh mục cha cao nhất
    public List<CategoryResponseDTO> getCategoryParent() {
        List<ProductCategoryEntity> rootCategories = categoryRepository.findByParentIsNullOrderByDisplayOrderAsc();
        return rootCategories.stream().map(categoryMapper::toDTONoChild).collect(Collectors.toList());
    }

    // EC: Lấy chi tiết
    public CategoryResponseDTO getCategoryDetail(String slug) {
        ProductCategoryEntity response = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục này"));
        return categoryMapper.toDTONoChild(response);
    }

    // EC: Lấy tất cả danh mục để khách hàng filter
    public List<CategoryResponseDTO> getCategoryTree() {
        List<ProductCategoryEntity> categories = categoryRepository.findAllByStatusOrderByDisplayOrderAsc(Status.ACTIVE);
        return categories.stream()
                .filter(cat -> cat.getParent() == null)
                .map(cat -> categoryMapper.toTree(cat, categories))
                .collect(Collectors.toList());
    }

    public String getFullCategoryPath(ProductCategoryEntity category) {
        if (category.getParent() == null) {
            return category.getSlug();
        }
        return getFullCategoryPath(category.getParent()) + "/" + category.getSlug();
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        if(categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tên danh mục này đã tồn tại!");
        }

        String slug = SlugUtils.makeSlug(request.getName());
        ProductCategoryEntity category = categoryMapper.toEntity(request, slug);

        if (request.getParentId() != null) {
            ProductCategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha để tạo mới"));
            category.setParent(parent);

            slug = getFullCategoryPath(category);
            category.setSlug(slug);
        }

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
        ProductCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cần cập nhật"));

        if (!category.getName().equals(request.getName())) {
            if(categoryRepository.existsByName(request.getName())) {
                throw new BadRequestException("Tên danh mục này đã tồn tại!");
            }
            category.setName(request.getName());
            category.setSlug(SlugUtils.makeSlug(request.getName()));
        }

        if (!category.getParent().getId().equals(request.getParentId())) {
            ProductCategoryEntity parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha để cập nhật"));
            category.setParent(parentCategory);

            String slug = getFullCategoryPath(category);
            category.setSlug(slug);
        }

        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setImageUrl(request.getImageUrl());

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        ProductCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục để xóa"));

        // if (productRepository.countByCategoryId(id) > 0) {
        //    throw new BadRequestException("Không thể xóa danh mục do đang có sản phẩm!");
        // }

        categoryRepository.delete(category);
    }
}
