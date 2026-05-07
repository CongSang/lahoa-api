package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String userImageUrl;
    private Set<String> roles;
    private Set<String> permissions;
}
