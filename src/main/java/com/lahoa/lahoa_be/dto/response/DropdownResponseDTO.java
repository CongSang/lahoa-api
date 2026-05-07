package com.lahoa.lahoa_be.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DropdownResponseDTO {

    private Long id;
    private String value;
    private String label;
}
