package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.MaterialReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaterialReceiptRepository extends
        JpaRepository<MaterialReceiptEntity, Long>,
        JpaSpecificationExecutor<MaterialReceiptEntity> {
}
