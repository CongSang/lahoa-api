package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE("Tạo mới"),
    UPDATE("Cập nhật"),
    DELETE("Xóa"),
    RESTORE("Khôi phục"),
    LOGIN("Đăng nhập"),
    LOGOUT("Đăng xuất");

    private final String displayName;

    AuditAction(String displayName) {
        this.displayName = displayName;
    }
}

//CREATE_ORDER
//CANCEL_ORDER
//APPROVE_ORDER
//CONFIRM_PAYMENT
//SHIP_ORDER
//COMPLETE_ORDER

//IMPORT_STOCK
//EXPORT_STOCK
//ADJUST_STOCK
//APPROVE_STOCKTAKE