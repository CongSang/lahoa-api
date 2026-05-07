package com.lahoa.lahoa_be.config;

import com.lahoa.lahoa_be.entity.RefreshTokenEntity;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import com.lahoa.lahoa_be.service.impl.JwtServiceImpl;
import com.lahoa.lahoa_be.service.impl.RefreshTokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtServiceImpl jwtService;
    private final RefreshTokenServiceImpl refreshTokenService;

    @Value("${app.activation.frontend.url}")
    private String frontendURL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized User Type");
            return;
        }
        Long userId = principal.getUser().getId();
        String token = jwtService.generateToken(principal);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userId);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendURL + "/auth/redirect")
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken.getToken())
                .build().encode().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}