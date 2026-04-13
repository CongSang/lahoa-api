package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByActivationToken(String activationToken);

    boolean existsByEmail(String email);
}
