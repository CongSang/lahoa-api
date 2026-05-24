package com.lahoa.lahoa_be.dto.request;

import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Data;

@Data
public class MaterialCategoryRequestDTO {

    private String code;
    private String name;
    private String description;
    private Status status;
}
