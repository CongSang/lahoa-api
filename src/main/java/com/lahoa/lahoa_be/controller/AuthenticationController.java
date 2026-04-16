package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import com.lahoa.lahoa_be.dto.request.AuthRequestDTO;
import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.dto.response.UserResponseDTO;
import com.lahoa.lahoa_be.securiry.UserPrincipal;
import com.lahoa.lahoa_be.service.AuthenticationService;
import com.lahoa.lahoa_be.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO userDTO) {
        log.info(">>> Yêu cầu đăng ký tài khoản: Email: {}", userDTO.getEmail());
        UserResponseDTO registeredUser = authService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token) {
        boolean isActivated = authService.activateUser(token);

        if(isActivated) {
            return ResponseEntity.ok("Profile activated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        log.info(">>> Yêu cầu đăng nhập: Email: {}", authRequest.getEmail());
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @GetMapping("/account-info")
    public ResponseEntity<UserResponseDTO> getPublicProfile() {
        UserResponseDTO user = authService.getPublicProfile(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(refreshTokenService.refreshNewToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        refreshTokenService.deleteByUserId(currentUser.getUser());
        return ResponseEntity.noContent().build();
    }
}
