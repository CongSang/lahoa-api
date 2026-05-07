package com.lahoa.lahoa_be.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditContext {

    private final HttpServletRequest request;

    public String getIpAddress() {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }

        return request.getRemoteAddr();
    }

    public String getEndpoint() {
        return request.getRequestURI();
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getUserAgent() {
        return request.getHeader("User-Agent");
    }
}
