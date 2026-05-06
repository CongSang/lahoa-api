package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.ProductPropertyValueEntity;
import com.lahoa.lahoa_be.entity.PropertyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPropertyValueRepository extends JpaRepository<ProductPropertyValueEntity, Long> {

    @Query("""
        SELECT ppv FROM ProductPropertyValueEntity ppv
        LEFT JOIN FETCH ppv.propertyValue pv
        LEFT JOIN FETCH pv.property
        WHERE ppv.product.id = :id
    """)
    List<ProductPropertyValueEntity> findPropertiesByProductId(@Param("id") Long id);

    @Query("""
        SELECT ppv FROM ProductPropertyValueEntity ppv
        LEFT JOIN FETCH ppv.propertyValue pv
        LEFT JOIN FETCH pv.property
        WHERE ppv.product.id IN :ids
    """)
    List<ProductPropertyValueEntity> findPropertiesByProductIds(@Param("ids") List<Long> ids);
}
