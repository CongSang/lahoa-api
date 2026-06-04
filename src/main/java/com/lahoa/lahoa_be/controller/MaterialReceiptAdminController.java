package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.MaterialReceiptFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialImportRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialReceiptResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.service.MaterialReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/material-receipts")
public class MaterialReceiptAdminController {

    private final MaterialReceiptService receiptService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<MaterialReceiptResponseDTO>> list(
            MaterialReceiptFilterRequestDTO filter
    ) {
        return ResponseEntity.ok(receiptService.list(filter));
    }

    @PostMapping
    public ResponseEntity<MaterialReceiptResponseDTO> create(
            @Valid @RequestBody MaterialImportRequestDTO request
    ) {
        return new ResponseEntity<>(receiptService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialReceiptResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.getById(id));
    }
}
