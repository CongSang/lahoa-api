package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductPropertyResponseDTO;
import com.lahoa.lahoa_be.entity.ProductEntity;
import com.lahoa.lahoa_be.entity.ProductPropertyValueEntity;
import com.lahoa.lahoa_be.entity.PropertyValueEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    public List<ProductPropertyResponseDTO> toProductPropertyDTO(List<ProductPropertyValueEntity> properties) {

        return properties.stream()
                .collect(Collectors.groupingBy(
                        pv -> pv.getPropertyValue().getProperty()
                ))
                .entrySet()
                .stream()
                .map(entry -> {

                    var property = entry.getKey();

                    return ProductPropertyResponseDTO.builder()
                            .id(property.getId())
                            .code(property.getCode())
                            .name(property.getName())
                            .values(
                                    entry.getValue().stream()
                                            .map(pv -> {
                                                var val = pv.getPropertyValue();
                                                return DropdownResponseDTO.builder()
                                                        .id(val.getId())
                                                        .value(val.getValue())
                                                        .label(val.getLabel())
                                                        .build();
                                            })
                                            .toList()
                            )
                            .build();
                })
                .toList();
    }
}
