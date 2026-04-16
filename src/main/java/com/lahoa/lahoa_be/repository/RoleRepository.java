package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>  {

    Optional<RoleEntity> findByName(String name);
}
