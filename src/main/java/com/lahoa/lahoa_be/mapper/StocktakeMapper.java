package com.lahoa.lahoa_be.mapper;

import com.lahoa.lahoa_be.dto.response.StocktakeDetailResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeResponseDTO;
import com.lahoa.lahoa_be.entity.StocktakeEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StocktakeMapper {

    public StocktakeResponseDTO toDTO(
            StocktakeEntity stocktake
    ) {

        return StocktakeResponseDTO
                .builder()
                .id(stocktake.getId())
                .code(stocktake.getCode())
                .warehouseId(stocktake.getWarehouse().getId())
                .warehouseName(stocktake.getWarehouse().getName())
                .warehouseStatus(stocktake.getWarehouse().getStatus())
                .note(stocktake.getNote())
                .totalItems(
                        stocktake.getDetails() == null
                                ? 0
                                : stocktake.getDetails().size()
                )
                .totalDifference(
                        stocktake.getDetails()
                                .stream()
                                .mapToInt(
                                        d -> Optional.ofNullable(
                                                d.getDifference()
                                        ).orElse(0)
                                )
                                .sum()
                )
                .createdBy(
                        stocktake.getCreatedBy() != null
                                ? stocktake.getCreatedBy().getFullName()
                                : null
                )
                .createdAt(stocktake.getCreatedAt())
                .details(
                        stocktake.getDetails()
                                .stream()
                                .map(d ->
                                        StocktakeDetailResponseDTO
                                                .builder()
                                                .materialId(d.getMaterial().getId())
                                                .materialCode(d.getMaterial().getCode())
                                                .materialName(d.getMaterial().getName())
                                                .materialStatus(d.getMaterial().getStatus())
                                                .unit(d.getMaterial().getUnit())
                                                .systemQty(d.getSystemQty())
                                                .actualQty(d.getActualQty())
                                                .difference(d.getDifference())
                                                .build()
                                )
                                .toList()
                )
                .build();
    }

    public StocktakeResponseDTO toListDTO(
            StocktakeEntity stocktake
    ) {

        return StocktakeResponseDTO
                .builder()
                .id(stocktake.getId())
                .code(stocktake.getCode())
                .warehouseId(stocktake.getWarehouse().getId())
                .warehouseName(stocktake.getWarehouse().getName())
                .warehouseStatus(stocktake.getWarehouse().getStatus())
                .note(stocktake.getNote())
                .totalItems(
                        stocktake.getDetails() == null
                                ? 0
                                : stocktake.getDetails().size()
                )
                .totalDifference(
                        stocktake.getDetails()
                                .stream()
                                .mapToInt(
                                        d -> Optional.ofNullable(
                                                d.getDifference()
                                        ).orElse(0)
                                )
                                .sum()
                )
                .createdBy(
                        stocktake.getCreatedBy() != null
                                ? stocktake.getCreatedBy().getFullName()
                                : null
                )
                .createdAt(stocktake.getCreatedAt())
                .details(null)
                .build();
    }
}
