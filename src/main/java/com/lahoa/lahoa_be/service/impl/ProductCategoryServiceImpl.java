package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.CategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.response.*;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.ProductCategoryMapper;
import com.lahoa.lahoa_be.repository.ProductCategoryMappingRepository;
import com.lahoa.lahoa_be.repository.ProductCategoryRepository;
import com.lahoa.lahoa_be.service.AuditLogService;
import com.lahoa.lahoa_be.service.CloudinaryService;
import com.lahoa.lahoa_be.service.ProductCategoryService;
import com.lahoa.lahoa_be.util.CompareUtils;
import com.lahoa.lahoa_be.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final AuditLogService auditService;
    private final ProductCategoryMapper categoryMapper;
    private final ProductCategoryMappingRepository mappingRepository;
    private final PagedMapper pagedMapper;

    // Admin: Lấy danh sách
    @Override
    public PagedResponseDTO<CategoryResponseDTO> list(
            CategoryFilterRequestDTO filter
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

        List<ProductCategoryEntity> categories = categoriesPaged.getContent();

        List<Long> ids = categories.stream()
                .map(ProductCategoryEntity::getId)
                .toList();

        Map<Long, Long> countMap = mappingRepository
                .countProductsByCategoryIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        Set<Long> parentSet = new HashSet<>(
                categoryRepository.findParentIdsHavingChildren(ids)
        );

        List<CategoryResponseDTO> dtoList = categories.stream()
                .map(c -> {
                    CategoryResponseDTO dto = categoryMapper.toDTO(c);

                    boolean isLeaf = !parentSet.contains(c.getId());

                    dto.setProductCount(
                            isLeaf ? countMap.getOrDefault(c.getId(), 0L) : 0
                    );

                    return dto;
                })
                .toList();

        return pagedMapper.toDTO(categoriesPaged, dtoList);
    }

    // Admin: Lấy tất cả danh mục cha cao nhất
    @Override
    public List<DropdownResponseDTO> getCategoryParent() {
        List<ProductCategoryEntity> rootCategories = categoryRepository.findByParentIsNullAndStatusNot(Status.DELETED);
        return rootCategories.stream().map(categoryMapper::toDropdown).collect(Collectors.toList());
    }

    @Override
    public List<ProductPropertyResponseDTO> getDropdownCategory() {
        List<ProductCategoryEntity> categories = categoryRepository.findAllByStatus(Status.ACTIVE);
        return categories.stream()
                .filter(cat -> cat.getParent() == null)
                .map(cat -> categoryMapper.toTreeDropdown(cat, categories))
                .collect(Collectors.toList());
    }

    private void validateName(String name, Long excludeId) {
        boolean exists = excludeId == null
                ? categoryRepository.existsByNameAndStatusNot(
                name,
                Status.DELETED
        )
                : categoryRepository.existsByNameAndIdNotAndStatusNot(
                name,
                excludeId,
                Status.DELETED
        );

        if (exists) {
            throw new BadRequestException("Tên danh mục đã tồn tại");
        }
    }

    private String getFullCategoryPath(ProductCategoryEntity category) {
        if (category.getParent() == null) {
            return category.getSlug();
        }
        return getFullCategoryPath(category.getParent()) + "/" + category.getSlug();
    }

    private ProductCategoryEntity getActiveCategory(Long id) {
        ProductCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (category.getStatus() == Status.DELETED) {
            throw new BadRequestException("Danh mục đã bị xóa");
        }

        return category;
    }

    private void updateChildrenPath(ProductCategoryEntity parent) {

        List<ProductCategoryEntity> children =
                categoryRepository.findByParentId(parent.getId());

        for (ProductCategoryEntity child : children) {

            child.setPath(getFullCategoryPath(child));

            updateChildrenPath(child);
        }
    }

    private String generateUniqueSlug(String base, Long excludeId) {
        String slug = base;
        int i = 1;

        while (categoryRepository.existsBySlugAndIdNot(slug, excludeId)) {
            slug = base + "-" + i++;
        }

        return slug;
    }

    @Override
    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        String name = request.getName().trim();
        validateName(name, null);
        try {
            String slug = generateUniqueSlug(SlugUtils.generateSlug(name), null);
            ProductCategoryEntity category = categoryMapper.toEntity(request, slug);

            if (request.getParentId() != null) {
                ProductCategoryEntity parent = getActiveCategory(request.getParentId());
                category.setParent(parent);
            }

            category.setPath(getFullCategoryPath(category));

            ProductCategoryEntity saved = categoryRepository.save(category);
            CategoryResponseDTO newCategory = categoryMapper.toDTO(saved);

            auditService.logAfterCommit(
                    AuditAction.CREATE,
                    AuditEntityType.CATEGORY,
                    saved.getId(),
                    saved.getName(),
                    null,
                    newCategory,
                    null
            );

            log.info("Created Category id={}", saved.getId());

            return newCategory;
        } catch (Exception e) {
            if (request.getImagePublicId() != null) {
                cloudinaryService.deleteImage(request.getImagePublicId());
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
        ProductCategoryEntity category = getActiveCategory(id);
        String name = request.getName().trim();
        validateName(name, category.getId());

        CategoryResponseDTO oldCategory = categoryMapper.toDTO(category);
        String oldPublicId = category.getImagePublicId();
        String newPublicId = request.getImagePublicId();
        try {
            boolean parentChanged = false;
            boolean nameChanged = !category.getName().equals(name);

            if (oldPublicId != null && !oldPublicId.equals(newPublicId)) {
                cloudinaryService.deleteAfterCommit(oldPublicId);
            }

            if (nameChanged) {
                category.setSlug(generateUniqueSlug(SlugUtils.generateSlug(name), category.getId()));
            }

            if (request.getParentId() != null) {
                if(request.getParentId().equals(id)) {
                    throw new BadRequestException("Danh mục cha bị trùng với dang mục hiện tại");
                }

                ProductCategoryEntity parentCategory = getActiveCategory(request.getParentId());
                if (!parentCategory.getId().equals(
                        category.getParent() != null ? category.getParent().getId() : null
                )) {
                    category.setParent(parentCategory);
                    parentChanged = true;
                }
            } else {
                if (category.getParent() != null) {
                    category.setParent(null);
                    parentChanged = true;
                }
            }

            category.setName(name);
            category.setDescription(
                    Optional.ofNullable(request.getDescription()).orElse("").trim());
            category.setDisplayOrder(request.getDisplayOrder());
            category.setImageUrl(request.getImageUrl());
            category.setImagePublicId(request.getImagePublicId());
            category.setStatus(request.getStatus());
            category.setSeoTitle(request.getSeoTitle());
            category.setSeoKeywords(request.getSeoKeywords());
            category.setSeoDescription(request.getSeoDescription());

            if (nameChanged || parentChanged) {
                category.setPath(getFullCategoryPath(category));

                updateChildrenPath(category);
            }

            ProductCategoryEntity saved = categoryRepository.save(category);
            CategoryResponseDTO newCategory = categoryMapper.toDTO(saved);

            Map<String, Object> changed =
                    CompareUtils.diff(oldCategory, newCategory);

            if (!changed.isEmpty()) {
                auditService.logAfterCommit(
                        AuditAction.UPDATE,
                        AuditEntityType.CATEGORY,
                        saved.getId(),
                        saved.getName(),
                        null,
                        null,
                        changed
                );
            }

            log.info("Updated Category id={}", id);

            return newCategory;
        } catch (Exception e) {
            if (newPublicId != null && !newPublicId.equals(oldPublicId)) {
                cloudinaryService.deleteImage(newPublicId);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductCategoryEntity category = getActiveCategory(id);

        if (categoryRepository.existsByParentId(id)) {
            throw new BadRequestException("Không thể xóa danh mục có danh mục con");
        }

        if (mappingRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Không thể xóa danh mục đang chứa sản phẩm");
        }

        cloudinaryService.deleteAfterCommit(category.getImagePublicId());
        mappingRepository.deleteByCategoryId(id);
        category.setImageUrl(null);
        category.setImagePublicId(null);
        category.setParent(null);
        category.setStatus(Status.DELETED);
        category.setPath(null);

        auditService.logAfterCommit(
                AuditAction.DELETE,
                AuditEntityType.CATEGORY,
                category.getId(),
                category.getName(),
                null,
                null,
                null
        );

        log.info("Soft deleted Category id={}", id);
    }

    @Override
    @Transactional
    public CategoryResponseDTO restore(Long id) {

        ProductCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (category.getStatus() != Status.DELETED) {
            throw new BadRequestException("Danh mục chưa bị xóa");
        }

        validateName(category.getName(), category.getId());

        category.setStatus(Status.ACTIVE);
        category.setParent(null);
        category.setSlug(generateUniqueSlug(category.getSlug(), category.getId()));
        category.setPath(category.getSlug());

        auditService.logAfterCommit(
                AuditAction.RESTORE,
                AuditEntityType.CATEGORY,
                category.getId(),
                category.getName(),
                null,
                null,
                null

        );

        log.info("Restored Category id={}", id);

        return categoryMapper.toDTO(category);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Status status) {
        ProductCategoryEntity category = getActiveCategory(id);
        CategoryResponseDTO oldCategory = categoryMapper.toDTO(category);

        category.setStatus(status);
        ProductCategoryEntity saved = categoryRepository.save(category);

        CategoryResponseDTO newCategory = categoryMapper.toDTO(saved);

        Map<String, Object> changed =
                CompareUtils.diff(oldCategory, newCategory);

        if (!changed.isEmpty()) {
            auditService.logAfterCommit(
                    AuditAction.UPDATE,
                    AuditEntityType.CATEGORY,
                    saved.getId(),
                    saved.getName(),
                    null,
                    null,
                    changed
            );
        }

        log.info("Changed status Category id={} to {}", id, status);
    }

    // EC: Lấy tất cả danh mục để khách hàng filter
    @Override
    public List<CategoryEcResponseDTO> getCategoryTree() {
        List<ProductCategoryEntity> categories = categoryRepository.findAllByStatus(Status.ACTIVE);
        return categories.stream()
                .filter(cat -> cat.getParent() == null)
                .map(cat -> categoryMapper.toTree(cat, categories))
                .collect(Collectors.toList());
    }

    // EC: Lấy chi tiết
    @Override
    public CategoryEcResponseDTO getBySlug(String slug) {
        ProductCategoryEntity response = categoryRepository.findBySlugAndStatus(slug, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
        return categoryMapper.toEcDTO(response);
    }
}
