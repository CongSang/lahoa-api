package com.lahoa.lahoa_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

    @NotNull(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống")
    private String password;
}
