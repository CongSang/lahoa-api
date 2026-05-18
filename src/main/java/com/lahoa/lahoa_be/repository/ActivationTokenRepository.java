package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.ActivationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository
        extends JpaRepository<ActivationTokenEntity, Long> {

    Optional<ActivationTokenEntity> findByToken(String token);

    void deleteByUserId(Long userId);

    Optional<ActivationTokenEntity> findByUserId(Long userId);
}
