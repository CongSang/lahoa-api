package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum Role {
    CUSTOMER("Khách hàng"),
    ADMIN("Quản trị"),
    STAFF("Nhân viên");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }
}
