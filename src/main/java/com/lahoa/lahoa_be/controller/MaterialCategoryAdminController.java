package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.MaterialCategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.filter.MaterialFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialCategoryRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialCategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.repository.MaterialCategoryRepository;
import com.lahoa.lahoa_be.service.MaterialCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/material-categories")
public class MaterialCategoryAdminController {

    private final MaterialCategoryService categoryService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<MaterialCategoryResponseDTO>> list(
            MaterialCategoryFilterRequestDTO filter) {
        return ResponseEntity.ok(categoryService.list(filter));
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<DropdownResponseDTO>> getDropdown() {
        return ResponseEntity.ok(categoryService.getDropdown());
    }

    @PostMapping
    public ResponseEntity<MaterialCategoryResponseDTO> create(
            @RequestBody MaterialCategoryRequestDTO req
    ){
        return new ResponseEntity<>(categoryService.create(req), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialCategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MaterialCategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialCategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<MaterialCategoryResponseDTO> restore(
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.restore(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaterialCategoryResponseDTO> updateStatus(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.updateStatus(id));
    }
}
