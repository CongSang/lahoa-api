package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.CodePrefix;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import com.lahoa.lahoa_be.dto.filter.MaterialReceiptFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialImportRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialReceiptResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.*;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.MaterialReceiptMapper;
import com.lahoa.lahoa_be.repository.MaterialReceiptRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.CodeGeneratorService;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import com.lahoa.lahoa_be.service.MaterialReceiptService;
import com.lahoa.lahoa_be.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialReceiptServiceImpl implements MaterialReceiptService {

    private final MaterialReceiptRepository receiptRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final MaterialInventoryService inventoryService;
    private final CodeGeneratorService codeService;
    private final MaterialReceiptMapper receiptMapper;

    @Override
    public MaterialReceiptResponseDTO create(MaterialImportRequestDTO req) {
        WarehouseEntity warehouse = warehouseRepository.findById(req.getWarehouseId())
                .orElseThrow();

        MaterialReceiptEntity receipt = MaterialReceiptEntity.builder()
                        .code(codeService.nextWithDate(CodePrefix.GRN))
                        .supplier(req.getSupplier())
                        .note(req.getNote())
                        .warehouse(warehouse)
                        .createdBy(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getId())
                        .details(new ArrayList<>())
                        .build();

        BigDecimal total = BigDecimal.ZERO;

        for (var d : req.getDetails()) {
            var material = materialRepository.findById(d.getMaterialId())
                            .orElseThrow();

            var detail = MaterialReceiptDetailEntity
                            .builder()
                            .receipt(receipt)
                            .material(material)
                            .quantity(d.getQuantity())
                            .unitCost(d.getUnitCost())
                            .build();

            receipt.getDetails().add(detail);

            total = total.add(d.getUnitCost()
                            .multiply(BigDecimal.valueOf(d.getQuantity()))
                    );

            inventoryService.importStock(
                    InventoryActionRequestDTO
                            .builder()
                            .materialId(d.getMaterialId())
                            .warehouseId(req.getWarehouseId())
                            .quantity(d.getQuantity())
                            .unitCost(d.getUnitCost())
                            .note(req.getNote())
                            .referenceType(InventoryReferenceType.PURCHASE_ORDER)
                            .referenceId(receipt.getCode())
                            .build()
            );
        }

        receipt.setTotalCost(total);

        receiptRepository.save(receipt);

        return receiptMapper.toDTO(receipt);
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialReceiptResponseDTO getById(Long id) {
        return receiptMapper.toDTO(
                receiptRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MaterialReceiptResponseDTO> list(MaterialReceiptFilterRequestDTO filter) {
        return null;
    };
}
