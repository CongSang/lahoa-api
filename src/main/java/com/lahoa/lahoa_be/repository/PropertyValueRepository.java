package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.PropertyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropertyValueRepository extends JpaRepository<PropertyValueEntity, Long> {
    @Query("""
        SELECT pv FROM PropertyValueEntity pv
        JOIN FETCH pv.property
        WHERE pv.id IN :ids
    """)
    List<PropertyValueEntity> findAllWithPropertyById(List<Long> ids);
}
