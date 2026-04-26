package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.ProductCategoryEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long>  {

    @EntityGraph(attributePaths = {"parent"})
    @Query("""
            SELECT c
            FROM ProductCategoryEntity c
            LEFT JOIN FETCH c.parent
            WHERE
                (
                    (:status IS NULL AND c.status <> 'DELETED')
                    OR (:status IS NOT NULL AND c.status = :status)
                )
                AND (:keyword IS NULL OR :keyword = ''
                     OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (
                    (:parentId IS NULL)
                    OR (:parentId = -1 AND c.parent IS NULL)
                    OR (c.parent.id = :parentId)
                )
            """)
    Page<ProductCategoryEntity> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") Status status,
            @Param("parentId") Long parentId,
            Pageable pageable);

    Optional<ProductCategoryEntity> findBySlug(String slug);

    @NullMarked
    Optional<ProductCategoryEntity> findById(Long id);

    Optional<ProductCategoryEntity> findByName(String name);

    boolean existsByParentId(Long parentId);

    List<ProductCategoryEntity> findByParentIsNullOrderByDisplayOrderAsc();

    List<ProductCategoryEntity> findAllByStatusOrderByDisplayOrderAsc(Status status);

    @Query("""
        SELECT c.id
        FROM ProductCategoryEntity c
        WHERE c.parent.id IN :ids
    """)
    List<Long> findParentIdsHavingChildren(List<Long> ids);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
