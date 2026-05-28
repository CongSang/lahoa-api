package com.lahoa.lahoa_be.common.enums;

public enum InventoryMovementType {

    IMPORT,     // nhập kho

    CONSUME,    // xuất bán

    ADJUST,     // kiểm kê chỉnh tay

    LOSS,       // hao hụt / hỏng / bỏ

    RETURN,     // hoàn đơn trả kho

    RESERVE,    // giữ hàng checkout

    RELEASE     // bỏ giữ hàng
}
