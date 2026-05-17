package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import com.lahoa.lahoa_be.dto.filter.ProductFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.ProductRequestDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.ProductResponseDTO;
import com.lahoa.lahoa_be.service.CloudinaryService;
import com.lahoa.lahoa_be.service.ProductService;
import com.lahoa.lahoa_be.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final VariantService variantService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductResponseDTO>> list(
            ProductFilterRequestDTO filter) {
        return ResponseEntity.ok(productService.list(filter));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponseDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ProductResponseDTO> restore(
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.restore(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponseDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        ProductStatus newStatus = ProductStatus.valueOf(body.get("status"));
        productService.updateStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/upload-signature")
    public Map<String, Object> getUploadSignature() {
        return cloudinaryService.generateSignature("lahoa/products");
    }
}
