package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.AuthProvider;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import com.lahoa.lahoa_be.dto.response.UserResponseDTO;
import com.lahoa.lahoa_be.entity.RefreshTokenEntity;
import com.lahoa.lahoa_be.entity.RoleEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.UnauthorizedException;
import com.lahoa.lahoa_be.mapper.UserMapper;
import com.lahoa.lahoa_be.repository.RoleRepository;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import com.lahoa.lahoa_be.dto.request.AuthRequestDTO;
import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    private final SnowflakeIdGenerator idGenerator;
    private final RoleRepository roleRepository;

    @Value("${app.activation.url}")
    private String backendURL;

    public UserResponseDTO registerUser(UserRequestDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("<<< Đăng ký thất bại: Email {} đã tồn tại trong hệ thống", userDTO.getEmail());
            throw new BadRequestException("Email này đã được sử dụng. Vui lòng chọn email khác!");
        }

        UserEntity newUser = userMapper.toEntity(userDTO);

        RoleEntity defaultRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role mặc định trong hệ thống!"));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(defaultRole);
        Long id = idGenerator.nextId();
        newUser.setId(id);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setActivationToken(UUID.randomUUID().toString());
        newUser.setProvider(AuthProvider.LOCAL);
        newUser.setRoles(roles);
        newUser.setStatus(Status.INACTIVE);
        newUser = userRepository.save(newUser);
        log.info("<<< Đăng ký thành công: Email: {} với ID: {}", userDTO.getEmail(), id);

        //send activation email
        String activationLink = backendURL + "/api/auth/activate?token=" + newUser.getActivationToken();
        String subject = "Xác thực tài khoản LA HOA website";
        String body = "Chào " + newUser.getFullName() + ",\n\n" +
                "Vui lòng click vào link sau để kích hoạt tài khoản của bạn:\n" + activationLink;
        mailService.sendEmail(newUser.getEmail(), subject, body);
        return userMapper.toDTO(newUser);
    }

    public UserPrincipal getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thực hiện thao tác này!");
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        return principal;
    }

    public UserResponseDTO getPublicProfile(String email) {
        UserPrincipal userPrincipal = getCurrentProfile();
        UserEntity currentUser = null;
        if(email == null) {
            currentUser = userPrincipal.getUser();
        } else {
            currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
        }

        Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toSet());

        Set<String> permissions = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .filter(auth -> !auth.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        UserResponseDTO response = userMapper.toDTO(currentUser);
        response.setRoles(roles);
        response.setPermissions(permissions);

        return response;
    }

    public boolean activateUser(String activationToken) {
        return userRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setStatus(Status.ACTIVE);
                    userRepository.save(profile);
                    return  true;
                })
                .orElse(false);
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO) throws BadCredentialsException {
        Optional<UserEntity> userOptional = userRepository.findByEmail(authRequestDTO.getEmail());
        UserEntity user;

        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(user.getPassword().isEmpty() && user.getProvider() == AuthProvider.GOOGLE) {
                log.warn("<<< Đăng nhập thất bại: Email: {} chưa có mật khẩu do đăng nhập qua Google", authRequestDTO.getEmail());
                throw new BadRequestException(
                        "Tài khoản này được tạo qua Google. Vui lòng đăng nhập bằng Google và vào cài đặt để thiết lập mật khẩu");
            }
        } else {
            log.warn("<<< Đăng nhập thất bại: Email: {} chưa chưa đăng kí", authRequestDTO.getEmail());
            throw  new UsernameNotFoundException("Tài khoản chưa được đăng ký!");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getEmail(),
                        authRequestDTO.getPassword()
                )
        );

        UserPrincipal principal = UserPrincipal.create(user);
        String jwtToken = jwtService.generateToken(principal);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getId());

        log.info("Email {} đăng nhập thành công vào hệ thống", authRequestDTO.getEmail());

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
