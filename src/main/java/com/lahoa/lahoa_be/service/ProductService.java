package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.ProductFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductResponseDTO;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import com.lahoa.lahoa_be.entity.ProductCategoryId;
import com.lahoa.lahoa_be.entity.ProductCategoryMappingEntity;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.ProductMapper;
import com.lahoa.lahoa_be.repository.ProductCategoryMappingRepository;
import com.lahoa.lahoa_be.repository.ProductCategoryRepository;
import com.lahoa.lahoa_be.repository.ProductRepository;
import com.lahoa.lahoa_be.specification.ProductSpecification;
import com.lahoa.lahoa_be.util.SlugUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMappingRepository mappingRepository;
    private final ProductMapper productMapper;
    private final PagedMapper pagedMapper;
    private final ProductCategoryMappingService mappingService;

    public PagedResponseDTO<ProductResponseDTO> list(ProductFilterRequestDTO filter) {
        Specification<ProductEntity> spec = ProductSpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<ProductEntity> productsPaged = productRepository.findAll(spec, pageable);

        List<ProductResponseDTO> content = productsPaged.getContent().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());

        return pagedMapper.toDTO(productsPaged, content);
    }

    private void validate(ProductRequestDTO req) {
        if (productRepository.existsByName(req.getName().trim())) {
            throw new BadRequestException("Tên sản phẩm đã tồn tại");
        }

        if (req.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Giá phải > 0");
        }

        if (req.getCategoryIds() == null || req.getCategoryIds().isEmpty()) {
            throw new BadRequestException("Phải chọn ít nhất 1 danh mục");
        }

        if (!req.getCategoryIds().contains(req.getPrimaryCategoryId())) {
            throw new BadRequestException("Primary category không hợp lệ");
        }

        // check category tồn tại & không deleted
        List<ProductCategoryEntity> categories =
                categoryRepository.findAllById(req.getCategoryIds());

        if (categories.size() != req.getCategoryIds().size()) {
            throw new BadRequestException("Danh mục không tồn tại");
        }

        boolean hasDeleted = categories.stream()
                .anyMatch(c -> c.getStatus() == Status.DELETED);

        if (hasDeleted) {
            throw new BadRequestException("Danh mục đã bị xóa");
        }
    }

    private String generateUniqueSlug(String base, Long excludeId) {
        String slug = base;
        int i = 1;

        while (productRepository.existsBySlugAndIdNot(slug, excludeId)) {
            slug = base + "-" + i++;
        }

        return slug;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO req) {

        validate(req);

        ProductEntity product = productMapper.toEntity(req);

        product.setSlug(generateUniqueSlug(
                SlugUtils.generateSlug(product.getName()),
                null
        ));

        product.setStatus(Status.ACTIVE);

        ProductEntity saved = productRepository.save(product);

        mappingService.syncCategories(saved, req);

        log.info("Created product id={}", saved.getId());

        return productMapper.toDTO(saved);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO req) {

        ProductEntity product = getActiveProduct(id);

        validate(req);

        boolean nameChanged = !product.getName().equals(req.getName().trim());

        productMapper.apply(product, req);

        if (nameChanged) {
            product.setSlug(generateUniqueSlug(
                    SlugUtils.generateSlug(product.getName()),
                    product.getId()
            ));
        }

        ProductEntity saved = productRepository.save(product);

        mappingService.syncCategories(saved, req);

        log.info("Updated product id={}", saved.getId());

        return productMapper.toDTO(saved);
    }

    private ProductEntity getActiveProduct(Long id) {
        return productRepository.findById(id)
                .filter(p -> p.getStatus() != Status.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    }

    public ProductResponseDTO getBySlug(String slug) {
        ProductEntity product = productRepository.findBySlug(slug)
                .filter(pr -> pr.getStatus() != Status.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        return productMapper.toDTO(product);
    }

    @Transactional
    public void delete(Long id) {
        ProductEntity product = getActiveProduct(id);
        product.setStatus(Status.DELETED);
        log.info("Soft deleted product id={}", id);
    }
}
