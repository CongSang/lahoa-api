package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
