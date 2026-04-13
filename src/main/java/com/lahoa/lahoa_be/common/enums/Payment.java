package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum Payment {
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    PARTIAL("Thanh toán một phần"), // Nếu khách đặt cọc trước
    FAILED("Thanh toán thất bại"),
    REFUNDED("Đã hoàn tiền");

    private final String displayName;

    Payment(String displayName) {
        this.displayName = displayName;
    }
}
