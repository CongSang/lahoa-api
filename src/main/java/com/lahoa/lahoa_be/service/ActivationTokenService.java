package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.ActivationStatus;
import com.lahoa.lahoa_be.entity.ActivationTokenEntity;
import com.lahoa.lahoa_be.entity.UserEntity;

import java.util.Optional;

public interface ActivationTokenService {

    ActivationTokenEntity create(UserEntity user);

    ActivationStatus activate(String token);

    Optional<ActivationTokenEntity> findValidToken(Long userId);
}
