package com.lahoa.lahoa_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DropdownResponseDTO {

    private Long id;
    private String value;
    private String label;
}
