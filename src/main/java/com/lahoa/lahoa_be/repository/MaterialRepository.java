package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository
        extends JpaRepository<MaterialEntity, Long>,
        JpaSpecificationExecutor<MaterialEntity> {

    boolean existsByCategoryId(Long id);

    boolean existsByCodeAndIdNot(String code, Long id);

    long countByCategoryId(Long id);

    @Query("""
        select m.category.id, count(m)
        from MaterialEntity m
        where m.category.id in :categoryIds
        group by m.category.id
    """)
    List<Object[]> countByCategoryIds(
            @Param("categoryIds")
            List<Long> categoryIds
    );
}
