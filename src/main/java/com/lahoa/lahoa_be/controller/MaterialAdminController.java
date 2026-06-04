package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.service.CloudinaryService;
import com.lahoa.lahoa_be.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/materials")
public class MaterialAdminController {

    private final MaterialService materialService;
    private final CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<MaterialResponseDTO> create(
            @RequestBody MaterialRequestDTO req
    ){
        return new ResponseEntity<>(materialService.create(req), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequestDTO request) {
        return ResponseEntity.ok(materialService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<MaterialResponseDTO> restore(
            @PathVariable Long id) {
        return ResponseEntity.ok(materialService.restore(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaterialResponseDTO> updateStatus(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.updateStatus(id));
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<DropdownResponseDTO>> getDropdown() {
        return ResponseEntity.ok(materialService.getDropdown());
    }

    @GetMapping("/upload-signature")
    public Map<String, Object> getUploadSignature() {
        return cloudinaryService.generateSignature("lahoa/materials");
    }
}
