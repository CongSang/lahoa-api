package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.AuthRequestDTO;
import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import com.lahoa.lahoa_be.dto.response.AuthResponseDTO;
import com.lahoa.lahoa_be.dto.response.UserResponseDTO;

import java.util.Map;

public interface CloudinaryService {

    /**
     * Lấy chữ kí ảnh
     */
    Map<String, Object> generateSignature(String folder);

    /**
     * Xóa ảnh
     */
    void deleteImage(String publicId);

    void deleteAfterCommit(String publicId);
}
