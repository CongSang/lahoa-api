package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    private String fullName;
    private String email;
    private String phone;
    private String userImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
