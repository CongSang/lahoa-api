package com.lahoa.lahoa_be.common.enums;

import lombok.Getter;

@Getter
public enum InventoryAction {
    STOCK_IN("Nhập hàng mới"),
    SALE_EXPORT("Xuất kho để bán"),       // (tự động theo đơn hàng)
    ADJUSTMENT("Admin điều chỉnh tay"),   // (do kiểm kho lệch)
    WASTAGE("Hao hụt do hoa héo, hỏng"),
    RETURN("Hoàn kho do khách hủy đơn");

    private final String displayName;

    InventoryAction(String displayName) {
        this.displayName = displayName;
    }
}
