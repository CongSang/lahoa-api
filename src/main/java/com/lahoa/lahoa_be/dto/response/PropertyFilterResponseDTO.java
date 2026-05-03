package com.lahoa.lahoa_be.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PropertyFilterResponseDTO {

    private Long id;
    private String code;
    private String name;

    private List<DropdownResponseDTO> values;
}
