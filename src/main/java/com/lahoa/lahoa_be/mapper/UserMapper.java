package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.common.enums.AuthProvider;
import com.lahoa.lahoa_be.common.enums.Role;
import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import com.lahoa.lahoa_be.dto.response.UserResponseDTO;
import com.lahoa.lahoa_be.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequestDTO userDTO) {
        if (userDTO == null) return null;
        return UserEntity.builder()
                .fullName(userDTO.getFullName())
                .phone(userDTO.getPhone())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .userImageUrl(userDTO.getUserImageUrl())
                .build();
    }

    public UserResponseDTO toDTO(UserEntity userEntity) {
        if (userEntity == null) return null;
        return UserResponseDTO.builder()
                .fullName(userEntity.getFullName())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .userImageUrl(userEntity.getUserImageUrl())
                .build();
    }
}
