package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.MaterialInventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaterialInventoryMovementRepository extends
        JpaRepository<MaterialInventoryMovementEntity, Long>,
        JpaSpecificationExecutor<MaterialInventoryMovementEntity> {
}
