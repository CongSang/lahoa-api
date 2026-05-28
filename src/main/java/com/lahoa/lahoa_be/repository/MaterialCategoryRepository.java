package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.MaterialCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MaterialCategoryRepository extends
        JpaRepository<MaterialCategoryEntity, Long>,
        JpaSpecificationExecutor<MaterialCategoryEntity> {

    List<MaterialCategoryEntity> findAllByStatus(Status status);
}
