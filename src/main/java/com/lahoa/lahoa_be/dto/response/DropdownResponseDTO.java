package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DropdownResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String value;
    private String label;
}
