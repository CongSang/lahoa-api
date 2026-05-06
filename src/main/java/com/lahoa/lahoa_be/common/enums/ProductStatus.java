package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    DRAFT("Bản nháp"),
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động"),
    DELETED("Đã xóa");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }
}
