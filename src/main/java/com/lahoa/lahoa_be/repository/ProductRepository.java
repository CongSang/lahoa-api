package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import com.lahoa.lahoa_be.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    @Query("""
        SELECT DISTINCT p FROM ProductEntity p
        LEFT JOIN FETCH p.categoryMappings cm
        LEFT JOIN FETCH cm.category
        WHERE p.slug = :slug
            AND p.status <> :status
    """)
    Optional<ProductEntity> findBySlugAndStatusWithCore(String slug, ProductStatus status);

    boolean existsByNameAndStatusNot(String name, ProductStatus status);

    boolean existsByNameAndIdNotAndStatusNot(String name, Long id, ProductStatus status);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query("""
        SELECT DISTINCT p FROM ProductEntity p
        LEFT JOIN FETCH p.categoryMappings cm
        LEFT JOIN FETCH cm.category
        WHERE p.id IN :ids
    """)
    List<ProductEntity> findProductsWithCategories(List<Long> ids);

    @Query("""
        SELECT DISTINCT p FROM ProductEntity p
        LEFT JOIN FETCH p.categoryMappings cm
        LEFT JOIN FETCH cm.category
        WHERE p.id = :id
    """)
    Optional<ProductEntity> findProductCore(Long id);
}
