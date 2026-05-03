package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.PropertyFilterResponseDTO;
import com.lahoa.lahoa_be.entity.PropertyEntity;
import com.lahoa.lahoa_be.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public List<PropertyFilterResponseDTO> getFilterProperties() {
        List<PropertyEntity> properties = propertyRepository.findByFilterableTrue();

        return properties.stream()
                .map(p -> PropertyFilterResponseDTO.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .name(p.getName())
                        .values(
                                p.getValues().stream()
                                        .map(v -> DropdownResponseDTO.builder()
                                                .id(v.getId())
                                                .value(v.getValue())
                                                .label(v.getLabel())
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
                )
                .toList();
    }
}
