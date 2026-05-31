package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository
        extends JpaRepository<WarehouseEntity, Long>,
        JpaSpecificationExecutor<WarehouseEntity> {

    Optional<WarehouseEntity> findByCode(String code);

    List<WarehouseEntity> findAllByStatus(Status status);
}
