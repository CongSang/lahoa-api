package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.response.*;
import com.lahoa.lahoa_be.service.CloudinaryService;
import com.lahoa.lahoa_be.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryEcResponseDTO>> getTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryEcResponseDTO> get(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getBySlug(slug));
    }
}
