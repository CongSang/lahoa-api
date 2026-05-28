package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.MaterialReceiptDetailResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialReceiptResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialReceiptEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MaterialReceiptMapper {

    public MaterialReceiptResponseDTO toDTO(MaterialReceiptEntity r) {

        return
                MaterialReceiptResponseDTO
                        .builder()
                        .id(r.getId())
                        .code(r.getCode())
                        .supplier(r.getSupplier())
                        .note(r.getNote())
                        .warehouseName(r.getWarehouse().getName())
                        .totalCost(
                                r.getDetails()
                                        .stream()
                                        .map(d -> d.getUnitCost()
                                                .multiply(
                                                        BigDecimal.valueOf(
                                                                d.getQuantity()
                                                        )
                                                )
                                        )
                                        .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add
                                        )
                        )
                        .details(
                                r.getDetails()
                                        .stream()
                                        .map(d ->
                                                MaterialReceiptDetailResponseDTO
                                                        .builder()
                                                        .materialName(d.getMaterial().getName())
                                                        .quantity(d.getQuantity())
                                                        .unitCost(d.getUnitCost())
                                                        .subtotal(d.getUnitCost()
                                                                .multiply(
                                                                        BigDecimal.valueOf(d.getQuantity())
                                                                )
                                                        )
                                                        .build()
                                        )
                                        .toList()
                        )
                        .createdAt(r.getCreatedAt())
                        .build();
    }
}
