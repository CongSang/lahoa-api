package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum VariantStatus {     // chưa public
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    OUT_OF_STOCK("Hết hàng"),
    DELETED("Đã xóa");

    private final String displayName;

    VariantStatus(String displayName) {
        this.displayName = displayName;
    }
}
