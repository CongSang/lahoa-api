package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum AuthProvider {
    LOCAL("Đăng nhập bằng hệ thống"),
    GOOGLE("Đăng nhập bằng Google"),
    FACEBOOK("Đăng nhập bằng Facebook");

    private final String displayName;

    AuthProvider(String displayName) {
        this.displayName = displayName;
    }
}

