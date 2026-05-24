package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WarehouseResponseDTO {

    private Long id;

    private String code;

    private String name;

    private String address;

    private Status status;

    private Long materialCount;
}
