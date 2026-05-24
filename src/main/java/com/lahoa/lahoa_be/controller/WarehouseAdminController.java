package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.MaterialCategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.filter.WarehouseFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.WarehouseRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.WarehouseResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/warehouses")
public class WarehouseAdminController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<WarehouseResponseDTO>> list(
            WarehouseFilterRequestDTO filter) {
        return ResponseEntity.ok(warehouseService.list(filter));
    }

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> create(
            @RequestBody WarehouseRequestDTO req
    ){
        return new ResponseEntity<>(warehouseService.create(req), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequestDTO request) {
        return ResponseEntity.ok(warehouseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<WarehouseResponseDTO> restore(
            @PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.restore(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WarehouseResponseDTO> updateStatus(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.updateStatus(id));
    }
}
