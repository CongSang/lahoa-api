package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MaterialInventoryRepository extends
        JpaRepository<MaterialInventoryEntity, Long>,
        JpaSpecificationExecutor<MaterialInventoryEntity> {

    Optional<MaterialInventoryEntity> findByMaterialIdAndWarehouseId(
            Long materialId,
            Long warehouseId
    );
}
