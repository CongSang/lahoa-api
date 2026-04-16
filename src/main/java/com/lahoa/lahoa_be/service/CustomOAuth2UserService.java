package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.AuthProvider;
import com.lahoa.lahoa_be.common.enums.Role;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.RoleEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.repository.RoleRepository;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import com.lahoa.lahoa_be.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SnowflakeIdGenerator idGenerator;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getProvider() == AuthProvider.LOCAL) {
                user = userRepository.save(user);
            }
        } else {
            user = new UserEntity();

            RoleEntity defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role mặc định trong hệ thống!"));
            Set<RoleEntity> roles = new HashSet<>();
            roles.add(defaultRole);
            user.setId(idGenerator.nextId());
            user.setEmail(email);
            user.setFullName(name);
            user.setStatus(Status.ACTIVE);
            user.setRoles(roles);
            user.setProvider(AuthProvider.GOOGLE);

            user = userRepository.save(user);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
}