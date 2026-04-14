package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.AuthProvider;
import com.lahoa.lahoa_be.common.enums.Role;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import com.lahoa.lahoa_be.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Lấy thông tin User mặc định từ Google
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
            return processOAuth2User(oidcUser);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OidcUser processOAuth2User(OidcUser oidcUser) {
        String email = oidcUser.getEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(oidcUser));

        return (OidcUser) UserPrincipal.create(user, oidcUser.getAttributes());
    }

    private UserEntity registerNewUser(OidcUser oidcUser) {
        UserEntity user = new UserEntity();
        user.setId(idGenerator.nextId());
        user.setEmail(oidcUser.getEmail());
        user.setFullName(oidcUser.getFullName());
        user.setUserImageUrl(oidcUser.getPicture());
        user.setPhone(oidcUser.getPhoneNumber());
        user.setStatus(Status.ACTIVE);
        user.setRole(Role.CUSTOMER);
        user.setProvider(AuthProvider.GOOGLE);
        return userRepository.save(user);
    }
}
