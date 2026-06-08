package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository
        extends JpaRepository<MaterialEntity, Long>,
        JpaSpecificationExecutor<MaterialEntity> {

    boolean existsByCategoryId(Long id);

    long countByCategoryIdAndStatusNot(Long id, Status status);

    List<MaterialEntity> findAllByStatus(Status status);

    @Query("""
        SELECT c,
               (SELECT COUNT(m.id) 
                FROM MaterialEntity m 
                WHERE m.category.id = c.id 
                  AND m.status <> 'DELETED') AS materialCount
        FROM MaterialCategoryEntity c
        WHERE
            (
                (:status IS NULL AND c.status <> 'DELETED')
                OR (:status IS NOT NULL AND c.status = :status)
            )
            AND (:keyword IS NULL OR :keyword = ''
                 OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Object[]> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") Status status,
            Pageable pageable);
}
