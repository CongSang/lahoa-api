package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.ProductPropertyValueEntity;
import com.lahoa.lahoa_be.entity.VariantPropertyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VariantPropertyValueRepository extends JpaRepository<VariantPropertyValueEntity, Long> {

    @Query("""
        SELECT ppv FROM VariantPropertyValueEntity ppv
        LEFT JOIN FETCH ppv.propertyValue pv
        LEFT JOIN FETCH pv.property
        WHERE ppv.variant.id = :id
    """)
    List<VariantPropertyValueEntity> findPropertiesByVariantId(@Param("id") Long id);

    @Query("""
        SELECT ppv FROM VariantPropertyValueEntity ppv
        LEFT JOIN FETCH ppv.propertyValue pv
        LEFT JOIN FETCH pv.property
        WHERE ppv.variant.id IN :ids
    """)
    List<VariantPropertyValueEntity> findPropertiesByVariantIds(@Param("ids") List<Long> ids);
}
