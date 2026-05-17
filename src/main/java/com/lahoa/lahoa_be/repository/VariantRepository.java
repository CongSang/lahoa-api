package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.VariantStatus;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VariantRepository
        extends JpaRepository<ProductVariantEntity, Long> {

    // Admin: lấy tất cả trừ DELETED
    @Query("""
         SELECT v
         FROM ProductVariantEntity v
         LEFT JOIN FETCH v.propertyValues vpv
         LEFT JOIN FETCH vpv.propertyValue
         LEFT JOIN FETCH vpv.propertyValue.property
         WHERE v.product.id IN :productId
         AND v.status <> :status
    """)
    List<ProductVariantEntity> findAllByProductIdAndStatusNot(
            Long productId,
            VariantStatus status
    );

    // Batch load admin list
    @Query("""
        SELECT DISTINCT v
        FROM ProductVariantEntity v
        LEFT JOIN FETCH v.propertyValues vpv
        LEFT JOIN FETCH vpv.propertyValue
        LEFT JOIN FETCH vpv.propertyValue.property
        WHERE v.product.id IN :ids
        AND v.status <> :status
    """)
    List<ProductVariantEntity> findAllByProductIdsAndStatusNot(
            List<Long> ids,
            VariantStatus status
    );

    // Ecommerce: chỉ ACTIVE
    @Query("""
        SELECT DISTINCT v
        FROM ProductVariantEntity v
        LEFT JOIN FETCH v.propertyValues vpv
        LEFT JOIN FETCH vpv.propertyValue
        LEFT JOIN FETCH vpv.propertyValue.property
        WHERE v.product.id = :productId
        AND v.status = :status
    """)
    List<ProductVariantEntity> findAllByProductIdAndStatus(
            Long productId,
            VariantStatus status
    );
}
