package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
//    COD("Thanh toán khi nhận hàng"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    E_WALLET("Ví điện tử (Momo/ZaloPay)"),
    VNPAY("Cổng thanh toán VNPay");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}
