package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.dto.filter.CategoryFilter;
import com.lahoa.lahoa_be.dto.request.CategoryRequestDTO;
import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import com.lahoa.lahoa_be.dto.response.CategoryEcResponseDTO;
import com.lahoa.lahoa_be.dto.response.CategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<CategoryResponseDTO>> getCategories(
            CategoryFilter filter) {
        return ResponseEntity.ok(categoryService.getCategories(filter));
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<CategoryResponseDTO>> getDetails() {
        return ResponseEntity.ok(categoryService.getCategoryParent());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO request) {
        return new ResponseEntity<>(categoryService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryEcResponseDTO>> getTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryEcResponseDTO> getDetails(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getCategoryDetails(slug));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Status newStatus = Status.valueOf(body.get("status"));
        categoryService.updateStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }
}
