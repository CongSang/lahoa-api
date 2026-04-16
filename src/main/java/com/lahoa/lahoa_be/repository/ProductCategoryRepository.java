package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long>  {

    @Query("SELECT c FROM ProductCategoryEntity c WHERE c.status = :status " +
            "AND (:keyword IS NULL OR c.name LIKE %:keyword% OR c.description LIKE %:keyword%) " +
            "AND (:parentId IS NULL OR c.parent.id = :parentId)")
    Page<ProductCategoryEntity> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") Status status,
            @Param("parentId") Long parentId,
            Pageable pageable);

    Optional<ProductCategoryEntity> findBySlug(String slug);

    @NullMarked
    Optional<ProductCategoryEntity> findById(Long id);

    boolean existsByName(String name);

    List<ProductCategoryEntity> findByParentIsNullOrderByDisplayOrderAsc();

    List<ProductCategoryEntity> findAllByStatusOrderByDisplayOrderAsc(Status status);
}
