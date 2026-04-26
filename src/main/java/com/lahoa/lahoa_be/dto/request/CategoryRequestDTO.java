package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDTO {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục tối đa 255 ký tự")
    private String name;

    @NotBlank(message = "Ảnh danh mục không được để trống")
    @Size(max = 1000)
    private String imageUrl;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    private Long parentId;

    private Integer displayOrder;

    @NotNull(message = "Trạng thái không được để trống")
    private Status status;

    @Size(max = 60, message = "SEO title tối đa 60 ký tự")
    private String seoTitle;

    @Size(max = 160, message = "SEO description tối đa 160 ký tự")
    private String seoDescription;

    @Size(max = 255, message = "SEO keywords tối đa 255 ký tự")
    private String seoKeywords;
}
