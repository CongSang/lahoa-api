package com.lahoa.lahoa_be.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;

public interface JwtService {

    /**
     * Generate JWT token
     */
    String generateToken(UserDetails userDetails);

    /**
     * Check token expired
     */
    boolean isTokenExpired(String token);

    /**
     * Extract username/email from token
     */
    String extractUsername(String token);

    /**
     * Validate token
     */
    boolean isTokenValid(
            String token,
            UserDetails userDetails
    );

    /**
     * Get signing key
     */
    Key getSignInKey();
}