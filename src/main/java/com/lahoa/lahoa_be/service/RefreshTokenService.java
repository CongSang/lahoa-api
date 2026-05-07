package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.entity.RefreshTokenEntity;
import com.lahoa.lahoa_be.entity.UserEntity;

public interface RefreshTokenService {

    /**
     * Tạo refresh token mới cho user
     */
    RefreshTokenEntity createRefreshToken(
            Long userId
    );

    /**
     * Verify token còn hạn hay không
     */
    RefreshTokenEntity verifyExpiration(
            RefreshTokenEntity token
    );

    /**
     * Refresh access token mới
     */
    AuthResponseDTO refreshNewToken(
            String refreshToken
    );

    /**
     * Logout user
     */
    void deleteByUserId(
            UserEntity user
    );

    /**
     * Dọn dẹp refresh token hết hạn
     */
    void purgeExpiredTokens();
}