package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.ProductCategoryMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCategoryMappingRepository extends JpaRepository<ProductCategoryMappingEntity, Long> {

    List<ProductCategoryMappingEntity> findByProductId(Long id);

    void deleteByProductId(Long id);

    @Query("""
        SELECT m.category.id, COUNT(DISTINCT m.product.id)
        FROM ProductCategoryMappingEntity m
        WHERE m.category.id IN :categoryIds
        GROUP BY m.category.id
    """)
    List<Object[]> countProductsByCategoryIds(List<Long> categoryIds);

    boolean existsByCategoryId(Long categoryId);

    @Modifying
    @Query("DELETE FROM ProductCategoryMappingEntity m WHERE m.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);
}
