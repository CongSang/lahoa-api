package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.response.PropertyFilterResponseDTO;
import com.lahoa.lahoa_be.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/filters")
    public ResponseEntity<List<PropertyFilterResponseDTO>> getFilters() {
        return ResponseEntity.ok(propertyService.getFilterProperties());
    }
}
