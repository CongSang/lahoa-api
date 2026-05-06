package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VariantRepository extends JpaRepository<ProductVariantEntity, Long> {

    @Query("""
        SELECT DISTINCT v FROM ProductVariantEntity v
        LEFT JOIN FETCH v.propertyValues vpv
        LEFT JOIN FETCH vpv.propertyValue
        WHERE v.product.id = :productId
    """)
    List<ProductVariantEntity> findVariantsByProductId(Long productId);

    @Query("""
        SELECT DISTINCT v FROM ProductVariantEntity v
        LEFT JOIN FETCH v.propertyValues vpv
        LEFT JOIN FETCH vpv.propertyValue
        WHERE v.product.id IN :ids
    """)
    List<ProductVariantEntity> findVariantsByProductIds(List<Long> ids);
}
