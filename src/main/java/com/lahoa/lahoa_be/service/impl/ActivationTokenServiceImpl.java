package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.ActivationStatus;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.ActivationTokenEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.repository.ActivationTokenRepository;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.service.ActivationTokenService;
import com.lahoa.lahoa_be.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivationTokenServiceImpl implements ActivationTokenService {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Value("${app.activation.url}")
    private String backendURL;

    @Override
    public ActivationTokenEntity create(UserEntity user) {

        activationTokenRepository.deleteByUserId(user.getId());

        ActivationTokenEntity token =
                ActivationTokenEntity.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiryDate(Instant.now().plus(48, ChronoUnit.HOURS))
                        .used(false)
                        .lastSentAt(Instant.now())
                        .build();

        return activationTokenRepository.save(token);
    }

    @Override
    public ActivationStatus activate(String rawToken) {

        Optional<ActivationTokenEntity> tokenOpt =
                activationTokenRepository.findByToken(rawToken);

        if (tokenOpt.isEmpty()) {
            return ActivationStatus.INVALID;
        }

        ActivationTokenEntity token = tokenOpt.get();

        if (token.isUsed()) {
            return ActivationStatus.INVALID;
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            return ActivationStatus.EXPIRED;
        }

        UserEntity user = token.getUser();

        user.setStatus(Status.ACTIVE);
        token.setUsed(true);

        userRepository.save(user);
        activationTokenRepository.save(token);

        return ActivationStatus.SUCCESS;
    }

    @Override
    public Optional<ActivationTokenEntity> findValidToken(Long userId) {
        return activationTokenRepository
                .findByUserId(userId)
                .filter(token ->
                        !token.isUsed()
                                &&
                                token.getExpiryDate()
                                        .isAfter(Instant.now())
                );
    }
}
