package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUserId(Long id);

    @Modifying
    @Transactional
    int deleteByExpiryDateBefore(Instant now);
}
