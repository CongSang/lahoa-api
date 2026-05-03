package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.PropertyEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {

    @EntityGraph(attributePaths = {"values"})
    List<PropertyEntity> findByFilterableTrue();
}
