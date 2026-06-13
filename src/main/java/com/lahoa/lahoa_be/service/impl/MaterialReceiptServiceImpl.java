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
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialReceiptRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.CodeGeneratorService;
import com.lahoa.lahoa_be.service.MaterialInventoryService;
import com.lahoa.lahoa_be.service.MaterialReceiptService;
import com.lahoa.lahoa_be.specification.MaterialReceiptSpecification;
import com.lahoa.lahoa_be.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
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
    private final PagedMapper pagedMapper;

    @Override
    public MaterialReceiptResponseDTO create(MaterialImportRequestDTO req) {
        WarehouseEntity warehouse = warehouseRepository.findById(req.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho tiếp nhận"));

        MaterialReceiptEntity receipt = MaterialReceiptEntity.builder()
                        .code(codeService.nextWithDate(CodePrefix.GRN))
                        .supplier(req.getSupplier())
                        .note(req.getNote())
                        .warehouse(warehouse)
                        .createdBy(SecurityUtils.getCurrentUser())
                        .details(new ArrayList<>())
                        .build();

        receipt = receiptRepository.save(receipt);

        BigDecimal total = BigDecimal.ZERO;

        for (var d : req.getDetails()) {
            var material = materialRepository.findById(d.getMaterialId())
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật liệu"));

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
                            .note("Nhập hàng " + receipt.getCode() + ": " + req.getNote())
                            .referenceType(InventoryReferenceType.PURCHASE_ORDER)
                            .referenceId(receipt.getId())
                            .referenceCode(receipt.getCode())
                            .build()
            );
        }

        receipt.setTotalCost(total);

        return receiptMapper.toDTO(receiptRepository.save(receipt));
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
        Specification<MaterialReceiptEntity> spec = MaterialReceiptSpecification.filter(filter);
        String sortField = filter.getSortField();

        if (sortField.equals("warehouseName")) {
            sortField = "warehouse.name";
        }

        if (sortField.equals("itemCount")) {
            sortField = "details";
        }

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<MaterialReceiptEntity> paged = receiptRepository.findAll(spec, pageable);

        List<MaterialReceiptResponseDTO> dtoList = paged.getContent()
                .stream().map(receiptMapper::toListDTO).toList();

        return pagedMapper.toDTO(paged, dtoList);
    };
}
