package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductPropertyResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String code;
    private String name;

    private List<DropdownResponseDTO> values;
}
