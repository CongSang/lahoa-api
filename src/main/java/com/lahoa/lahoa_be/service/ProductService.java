package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.ProductFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductResponseDTO;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.ProductPropertyValueEntity;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.mapper.ProductMapper;
import com.lahoa.lahoa_be.repository.ProductCategoryRepository;
import com.lahoa.lahoa_be.repository.ProductPropertyValueRepository;
import com.lahoa.lahoa_be.repository.ProductRepository;
import com.lahoa.lahoa_be.repository.VariantRepository;
import com.lahoa.lahoa_be.specification.ProductSpecification;
import com.lahoa.lahoa_be.util.SlugUtils;
import com.lahoa.lahoa_be.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final VariantRepository variantRepository;
    private final ProductPropertyValueRepository productPropertyValueRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;
    private final PagedMapper pagedMapper;
    private final ProductCategoryMappingService mappingService;
    private final PropertyService propertyService;
    private final VariantService variantService;
    private final SnowflakeIdGenerator idGenerator;

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

        List<Long> ids = productsPaged.getContent().stream()
                .map(ProductEntity::getId)
                .toList();

        if (ids.isEmpty()) {
            return pagedMapper.toDTO(productsPaged, List.of());
        }

        List<ProductEntity> products = productRepository.findProductsWithCategories(ids);

        List<ProductVariantEntity> variants = variantRepository.findVariantsByProductIds(ids);

        List<ProductPropertyValueEntity> props =
                productPropertyValueRepository.findPropertiesByProductIds(ids);

        Map<Long, List<ProductPropertyValueEntity>> propertyMap =
                props.stream().collect(Collectors.groupingBy(
                        p -> p.getProduct().getId()
                ));

        Map<Long, List<ProductVariantEntity>> variantMap = variants.stream()
                .collect(Collectors.groupingBy(v -> v.getProduct().getId()));

        List<ProductResponseDTO> content = products.stream()
                .map(p -> productMapper.toDTO(
                        p,
                        variantMap.getOrDefault(p.getId(), List.of()),
                        propertyMap.getOrDefault(p.getId(), List.of())
                ))
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
            throw new BadRequestException("Danh mục chính phải nằm trong danh sách danh mục được chọn");
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

    private ProductResponseDTO buildResponse(Long productId) {
        ProductEntity full = productRepository.findProductCore(productId).orElseThrow();
        List<ProductVariantEntity> variants =
                variantRepository.findVariantsByProductId(productId);
        List<ProductPropertyValueEntity> productPropertyValue =
                productPropertyValueRepository.findPropertiesByProductId(productId);
        return productMapper.toDTO(full, variants, productPropertyValue);
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

        Long id = idGenerator.nextId();
        product.setId(id);
        product.setStatus(ProductStatus.ACTIVE);

        ProductEntity saved = productRepository.save(product);

        mappingService.syncCategories(saved, req);
        propertyService.syncProductProperties(product, req);
        variantService.syncVariants(product, req);

        log.info("Created Product id={}", saved.getId());

        return buildResponse(saved.getId());
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO req) {

        ProductEntity product = getActiveProduct(id);

        validate(req);

        if (req.getImagePublicId() != null &&
                !req.getImagePublicId().equals(product.getImagePublicId())) {

            cloudinaryService.deleteAfterCommit(product.getImagePublicId());
        }

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
        propertyService.syncProductProperties(product, req);
        variantService.syncVariants(product, req);

        log.info("Updated Product id={}", saved.getId());

        return buildResponse(saved.getId());
    }

    private ProductEntity getActiveProduct(Long id) {
        return productRepository.findById(id)
                .filter(p -> p.getStatus() != ProductStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    }

    public ProductResponseDTO getBySlug(String slug) {
        ProductEntity product = productRepository.findBySlugWithCore(slug)
                .filter(pr -> pr.getStatus() != ProductStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        List<ProductVariantEntity> variants =
                variantRepository.findVariantsByProductId(product.getId());


        List<ProductPropertyValueEntity> productPropertyValue =
                productPropertyValueRepository.findPropertiesByProductId(product.getId());

        return productMapper.toDTO(product, variants, productPropertyValue);
    }

    @Transactional
    public void delete(Long id) {
        ProductEntity product = getActiveProduct(id);

        cloudinaryService.deleteAfterCommit(product.getImagePublicId());
        product.setMainImage(null);
        product.setImagePublicId(null);
        product.setStatus(ProductStatus.DELETED);
        log.info("Soft deleted Product id={}", id);
    }
}
