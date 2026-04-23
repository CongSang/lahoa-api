package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.CategoryFilter;
import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.response.CategoryEcResponseDTO;
import com.lahoa.lahoa_be.dto.response.CategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper categoryMapper;
    private final PagedMapper pagedMapper;

    // Admin: Lấy danh sách
    public PagedResponseDTO<CategoryResponseDTO> getCategories(
            CategoryFilter filter
    ) {
        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<ProductCategoryEntity> categoriesPaged = categoryRepository
                .findByFilters(filter.getKeyword(), filter.getStatus(), filter.getParentId(), pageable);

        List<CategoryResponseDTO> content = categoriesPaged.getContent().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        return pagedMapper.toDTO(categoriesPaged, content);
    }

    // Admin: Lấy tất cả danh mục cha cao nhất
    public List<DropdownResponseDTO> getCategoryParent() {
        List<ProductCategoryEntity> rootCategories = categoryRepository.findByParentIsNullOrderByDisplayOrderAsc();
        return rootCategories.stream().map(categoryMapper::toDropdown).collect(Collectors.toList());
    }

    // EC: Lấy chi tiết
    public CategoryEcResponseDTO getCategoryDetails(String slug) {
        ProductCategoryEntity response = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục này"));
        return categoryMapper.toEcDTO(response);
    }

    // EC: Lấy tất cả danh mục để khách hàng filter
    public List<CategoryEcResponseDTO> getCategoryTree() {
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
        Optional<ProductCategoryEntity> existing = categoryRepository.findByName(request.getName());
        ProductCategoryEntity category;

        String slug = SlugUtils.makeSlug(request.getName());
        if (existing.isPresent()) {
            category = existing.get();

            if (category.getStatus() == Status.DELETED) {
                category.setStatus(Status.ACTIVE);
                category.setSlug(slug);
                category.setDescription(request.getDescription());
                category.setDisplayOrder(request.getDisplayOrder());
                category.setImageUrl(request.getImageUrl());
            }

            throw new BadRequestException("Tên danh mục đã tồn tại");
        } else {
            category = categoryMapper.toEntity(request, slug);
        }

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
        Optional<ProductCategoryEntity> existing = categoryRepository.findByName(request.getName());

        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new BadRequestException("Tên danh mục đã tồn tại");
        }

        category.setSlug(SlugUtils.makeSlug(category.getName()));

        if (request.getParentId() != null) {
            ProductCategoryEntity parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha để cập nhật"));
            category.setParent(parentCategory);

            String slug = getFullCategoryPath(category);
            category.setSlug(slug);
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setImageUrl(request.getImageUrl());
        category.setStatus(request.getStatus());

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        ProductCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục để xóa"));

        if (categoryRepository.existsByParentId(id)) {
            throw new BadRequestException("Không thể xóa do danh mục có chứa danh mục con");
        }

        // if (productRepository.countByCategoryId(id) > 0) {
        //    throw new BadRequestException("Không thể xóa danh mục đang có sản phẩm!");
        // }

        category.setStatus(Status.DELETED);
    }

    @Transactional
    public void updateStatus(Long id, Status status) {
        ProductCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cần cập nhật"));
        category.setStatus(status);
    }
}
