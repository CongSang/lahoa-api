package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    DELETED("Đã xóa");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
