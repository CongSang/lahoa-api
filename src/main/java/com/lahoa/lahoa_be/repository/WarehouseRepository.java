package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WarehouseRepository
        extends JpaRepository<WarehouseEntity, Long>,
        JpaSpecificationExecutor<WarehouseEntity> {

    Optional<WarehouseEntity> findByCode(String code);
}
