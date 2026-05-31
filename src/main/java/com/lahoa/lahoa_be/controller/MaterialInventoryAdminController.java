package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.MaterialInventoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialWarehouseInventoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/inventories")
public class MaterialInventoryAdminController {

    private final MaterialInventoryService inventoryService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<MaterialInventorySummaryResponseDTO>> list(
            MaterialInventoryFilterRequestDTO req
    ){
        return ResponseEntity.ok(inventoryService.list(req));
    }

    @GetMapping("/{materialId}/warehouses")
    public ResponseEntity<List<MaterialWarehouseInventoryResponseDTO>> getWarehouseInventories(
            @PathVariable Long materialId
    ) {
        return ResponseEntity.ok(
                inventoryService.getWarehouseInventories(materialId)
        );
    }
}
