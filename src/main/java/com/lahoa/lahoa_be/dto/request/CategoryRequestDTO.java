package com.lahoa.lahoa_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequestDTO {

    @NotNull(message = "Tên danh mục không được để trống")
    private String name;

    @NotNull(message = "Ảnh danh mục không được để trống")
    private String imageUrl;

    private Long parentId;
    private String description;
    private Integer displayOrder;
}
