package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.dto.request.UserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private UserResponseDTO user;
    private String token;
    private String refreshToken;
}
