package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.MaterialInventoryMovementFilterRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryMovementResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.service.MaterialInventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/inventory-movements")
public class InventoryMovementAdminController {

    private final MaterialInventoryMovementService movementService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<MaterialInventoryMovementResponseDTO>> list(
            MaterialInventoryMovementFilterRequestDTO req
    ) {
        return ResponseEntity.ok(movementService.list(req));
    }
}
