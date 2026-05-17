package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.entity.RefreshTokenEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.repository.RefreshTokenRepository;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import com.lahoa.lahoa_be.service.AuditLogService;
import com.lahoa.lahoa_be.service.JwtService;
import com.lahoa.lahoa_be.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.expirationtime}")
    private Long refreshTokenDurationMs;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuditLogService auditService;

    @Override
    public RefreshTokenEntity createRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại");
        }
        return token;
    }

    @Override
    public AuthResponseDTO refreshNewToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    UserPrincipal principal = UserPrincipal.create(user);
                    String newToken = jwtService.generateToken(principal);

                    return AuthResponseDTO.builder()
                            .token(newToken)
                            .refreshToken(refreshToken)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại hoặc đã bị xóa!"));
    }

    @Override
    @Transactional
    public void deleteByUserId(UserEntity user) {
        refreshTokenRepository.deleteByUserId(user.getId());

        auditService.log(
                AuditAction.LOGOUT,
                AuditEntityType.USER,
                user.getId(),
                user.getFullName(),
                null,
                null,
                null
        );
    }

    /**
     * Tự động xóa các Token đã hết hạn trong Database
     * cron = "0 0 0 * * ?" : Chạy vào đúng 00:00:00 mỗi ngày
     */
    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeExpiredTokens() {
        Instant now = Instant.now();
        int deletedCount = refreshTokenRepository.deleteExpired(now);
        log.info("Đã dọn dẹp {} Refresh Tokens hết hạn vào lúc {}", deletedCount, now);
    }
}
