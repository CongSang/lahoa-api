package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.AuthRequestDTO;
import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.dto.response.UserResponseDTO;
import com.lahoa.lahoa_be.securiry.UserPrincipal;

public interface AuthenticationService {

    /**
     * Đăng ký tài khoản mới
     */
    UserResponseDTO register(UserRequestDTO userDTO);

    /**
     * Đăng nhập
     */
    AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO);

    /**
     * Kích hoạt tài khoản
     */
    boolean activate(String activationToken);

    /**
     * Lấy profile hiện tại
     */
    UserPrincipal getCurrentProfile();

    /**
     * Lấy public profile theo email
     */
    UserResponseDTO getPublicProfile(String email);
}