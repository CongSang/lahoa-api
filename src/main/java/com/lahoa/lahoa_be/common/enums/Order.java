package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum Order {
    PENDING("Mới"), //Chờ xác nhận
    CONFIRMED("Đã xác nhận"),
    PROCESSING("Đang cắm hoa"),
    READY_FOR_DELIVERY("Chờ giao hàng"),
    SHIPPING("Đang giao hàng"),
    DELIVERED("Đã giao thành công"),
    CANCELLED("Đã hủy"),
    REFUNDED("Đã hoàn tiền");

    private final String displayName;

    Order(String displayName) {
        this.displayName = displayName;
    }
}
