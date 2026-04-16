package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    private String fullName;
    private String email;
    private String phone;
    private String userImageUrl;
    private Set<String> roles;
    private Set<String> permissions;
}
