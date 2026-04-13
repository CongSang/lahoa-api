package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {

    @NotNull(message = "Tên không được để trống")
    private String fullName;

    @NotNull(message = "Mật khẩu không được để trống")
    private String password;

    @Email(message = "Định dạng Email không hợp lệ")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@gmail.com$", message = "Email không đúng định dạng")
    private String email;

    @NotNull(message = "Số điện thoại không được để trống")
    private String phone;

    private String userImageUrl;
}
