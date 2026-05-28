package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.dto.request.InventoryActionRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import com.lahoa.lahoa_be.exception.BadRequestException;
import com.lahoa.lahoa_be.exception.ResourceNotFoundException;
import com.lahoa.lahoa_be.mapper.MaterialInventoryMapper;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialInventoryRepository;
import com.lahoa.lahoa_be.repository.MaterialRepository;
import com.lahoa.lahoa_be.repository.WarehouseRepository;
import com.lahoa.lahoa_be.service.MaterialInventoryMovementService;
import com.lahoa.lahoa_be.service.MaterialStockReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialStockReservationServiceImpl implements MaterialStockReservationService {

    private final MaterialInventoryRepository inventoryRepository;
    private final MaterialInventoryMapper inventoryMapper;
    private final MaterialInventoryMovementService movementService;

    private void validateAvailable(MaterialInventoryEntity inv, int qty) {
        if (inv.getOnHand() - inv.getReserved() < qty) {
            throw new BadRequestException(
                    "Không đủ tồn kho"
            );
        }
    }

    private MaterialInventoryEntity getInventory(InventoryActionRequestDTO req) {
        return inventoryRepository
                .findByMaterialIdAndWarehouseId(
                        req.getMaterialId(),
                        req.getWarehouseId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy tồn kho"
                        )
                );
    }

    @Override
    public MaterialInventoryResponseDTO reserve(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getInventory(req);

        validateAvailable(inventory, req.getQuantity());

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setReserved(inventory.getReserved() + req.getQuantity());

        movementService.log(
                inventory,
                InventoryMovementType.IMPORT,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );

        return inventoryMapper.toDTO(inventory);
    }

    @Override
    public MaterialInventoryResponseDTO release(InventoryActionRequestDTO req) {
        MaterialInventoryEntity inventory = getInventory(req);

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setReserved(
                Math.max(0, inventory.getReserved() - req.getQuantity())
        );

        movementService.log(
                inventory,
                InventoryMovementType.IMPORT,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );

        return inventoryMapper.toDTO(inventory);
    }

    @Override
    public MaterialInventoryResponseDTO consume(InventoryActionRequestDTO req) {
        var inventory = getInventory(req);

        validateAvailable(inventory, req.getQuantity());

        int beforeOnHand = inventory.getOnHand();

        int beforeReserved = inventory.getReserved();

        inventory.setOnHand(inventory.getOnHand() - req.getQuantity());

        inventory.setReserved(
                Math.max(0, inventory.getReserved() - req.getQuantity())
        );

        movementService.log(
                inventory,
                InventoryMovementType.IMPORT,
                req.getQuantity(),
                beforeOnHand,
                beforeReserved,
                req.getNote(),
                req.getReferenceType(),
                req.getReferenceId()
        );

        return inventoryMapper.toDTO(inventory);
    }
}
