package com.lahoa.lahoa_be.util;

import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static UserEntity getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }

        return principal.getUser();
    }
}