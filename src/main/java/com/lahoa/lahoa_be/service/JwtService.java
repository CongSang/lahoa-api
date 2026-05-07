package com.lahoa.lahoa_be.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.function.Function;

public interface JwtService {

    /**
     * Generate JWT token
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extract all claims from token
     */
    Claims extractAllClaims(String token);

    /**
     * Extract specific claim
     */
    <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    );

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