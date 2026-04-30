package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequestDTO {

    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String name;

    @NotBlank(message = "Ảnh sản phẩm không được để trống")
    @Size(max = 1000)
    private String imageUrl;

    private String imagePublicId;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Giá phải > 0")
    @Digits(integer = 12, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal price;

    @NotEmpty(message = "Phải chọn ít nhất 1 danh mục")
    private List<@NotNull(message = "CategoryId không hợp lệ") Long> categoryIds;

    @NotNull(message = "Trạng thái không được để trống")
    private Status status;

    private Integer displayOrder;

    @NotNull(message = "Phải chọn danh mục chính")
    private Long primaryCategoryId;

    @Size(max = 60, message = "SEO title tối đa 60 ký tự")
    private String seoTitle;

    @Size(max = 160, message = "SEO description tối đa 160 ký tự")
    private String seoDescription;

    @Size(max = 255, message = "SEO keywords tối đa 255 ký tự")
    private String seoKeywords;
}
