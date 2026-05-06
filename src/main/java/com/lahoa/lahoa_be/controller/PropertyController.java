package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.response.ProductPropertyResponseDTO;
import com.lahoa.lahoa_be.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/filters")
    public ResponseEntity<List<ProductPropertyResponseDTO>> getProperties(
            @RequestParam(defaultValue = "true") boolean isFilterable
    ) {
        return ResponseEntity.ok(propertyService.getProperties(isFilterable));
    }
}
