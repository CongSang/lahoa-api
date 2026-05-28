package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.MaterialInventoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventorySummaryResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialEntity;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MaterialInventoryMapper {

    private final MaterialMapper materialMapper;

    public MaterialInventoryResponseDTO toDTO(MaterialInventoryEntity entity) {
        if (entity == null) return null;

        int available = entity.getAvailable();

        int threshold = entity.getMaterial().getLowStockThreshold();

        return MaterialInventoryResponseDTO.builder()
                .id(entity.getId())
                .material(materialMapper.toDTO(entity.getMaterial()))
                .warehouseId(entity.getWarehouse().getId())
                .warehouseName(entity.getWarehouse().getName())
                .onHand(entity.getOnHand())
                .reserved(entity.getReserved())
                .available(available)
                .lowStockThreshold(threshold)
                .lowStock(available <= threshold)
                .build();
    }

    public MaterialInventorySummaryResponseDTO mapSummary(MaterialEntity material) {

        List<MaterialInventoryEntity> inventories = material.getInventories();

        int onHand = inventories.stream()
                        .mapToInt(MaterialInventoryEntity::getOnHand)
                        .sum();

        int reserved = inventories.stream()
                        .mapToInt(MaterialInventoryEntity::getReserved)
                        .sum();

        int available = onHand - reserved;

        BigDecimal totalInventoryValue =
                inventories.stream()
                        .map(i ->

                                Optional.ofNullable(
                                        i.getCostPrice()
                                ).orElse(
                                        BigDecimal.ZERO
                                ).multiply(
                                        BigDecimal.valueOf(
                                                i.getOnHand()
                                        )
                                )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal costPrice =
                onHand <= 0
                        ? BigDecimal.ZERO
                        : totalInventoryValue.divide(
                        BigDecimal.valueOf(onHand),
                        2,
                        RoundingMode.HALF_UP
                );

        boolean lowStock =
                inventories.stream()
                        .anyMatch(i -> {
                            int warehouseAvailable = i.getOnHand() - i.getReserved();

                            return warehouseAvailable
                                    <=
                                    material.getLowStockThreshold();
                        });

        boolean outOfStock =
                inventories.stream()
                        .anyMatch(i -> {
                            int warehouseAvailable = i.getOnHand() - i.getReserved();

                            return warehouseAvailable <= 0;
                        });

        return MaterialInventorySummaryResponseDTO
                        .builder()
                        .id(material.getId())
                        .categoryId(material.getCategory().getId() )
                        .categoryName(material.getCategory().getName())
                        .code(material.getCode())
                        .name(material.getName())
                        .unit(material.getUnit())
                        .thumbnail(material.getThumbnail())
                        .thumbnailPublicId(material.getThumbnailPublicId())
                        .status(material.getStatus())
                        .warehouseCount(inventories.size())
                        .onHand(onHand)
                        .reserved(reserved)
                        .available(available)
                        .costPrice(costPrice)
                        .lowStockThreshold(material.getLowStockThreshold())
                        .hasLowStockWarehouse(lowStock)
                        .hasOutOfStockWarehouse(outOfStock)
                        .build();
    }
}
