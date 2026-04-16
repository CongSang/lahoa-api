package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.entity.RefreshTokenEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.mapper.UserMapper;
import com.lahoa.lahoa_be.repository.RefreshTokenRepository;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${jwt.expirationtime}")
    private Long refreshTokenDurationMs;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public RefreshTokenEntity createRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại");
        }
        return token;
    }

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

    @Transactional
    public void deleteByUserId(UserEntity user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    /**
     * Tự động xóa các Token đã hết hạn trong Database
     * cron = "0 0 0 * * ?" : Chạy vào đúng 00:00:00 mỗi ngày
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeExpiredTokens() {
        Instant now = Instant.now();
        int deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(now);
        System.out.println("Đã dọn dẹp " + deletedCount + " Refresh Tokens hết hạn vào lúc " + now);
    }
}
