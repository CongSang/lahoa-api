package com.lahoa.lahoa_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MaterialImportRequestDTO {

    @NotNull(message = "Kho không được để trống")
    private Long warehouseId;

    @NotNull(message = "Nhà cung cấp không được để trống")
    private String supplier;

    private String note;

    @NotEmpty(message = "Danh sách vật liệu nhập kho không được để trống")
    @Valid
    private List<MaterialImportDetailRequestDTO> details;
}
