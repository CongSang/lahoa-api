package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.response.ProductResponseDTO;
import com.lahoa.lahoa_be.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponseDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }
}
