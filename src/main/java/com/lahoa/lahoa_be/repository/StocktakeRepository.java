package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.StocktakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StocktakeRepository
        extends JpaRepository<StocktakeEntity, Long>,
        JpaSpecificationExecutor<StocktakeEntity> {
}
