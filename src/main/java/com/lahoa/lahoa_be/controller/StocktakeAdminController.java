package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.MaterialReceiptFilterRequestDTO;
import com.lahoa.lahoa_be.dto.filter.StocktakeFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.CreateStocktakeRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialImportRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialReceiptResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeMaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeResponseDTO;
import com.lahoa.lahoa_be.service.MaterialReceiptService;
import com.lahoa.lahoa_be.service.StocktakeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/stocktakes")
public class StocktakeAdminController {

    private final StocktakeService stocktakeService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<StocktakeResponseDTO>> list(
            StocktakeFilterRequestDTO filter
    ) {
        return ResponseEntity.ok(stocktakeService.list(filter));
    }

    @PostMapping
    public ResponseEntity<StocktakeResponseDTO> create(
            @Valid @RequestBody CreateStocktakeRequestDTO request
    ) {
        return new ResponseEntity<>(stocktakeService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StocktakeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stocktakeService.detail(id));
    }

    @GetMapping("/warehouses/{warehouseId}/materials")
    public List<StocktakeMaterialResponseDTO> getMaterialsForStocktake(
            @PathVariable Long warehouseId
    ) {
        return stocktakeService.getMaterialsForStocktake(warehouseId);
    }
}
