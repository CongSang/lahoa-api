package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.CodePrefix;
import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import com.lahoa.lahoa_be.dto.filter.MaterialInventoryMovementFilterRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialInventoryMovementResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.MaterialInventoryEntity;
import com.lahoa.lahoa_be.entity.MaterialInventoryMovementEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.mapper.MaterialInventoryMovementMapper;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.repository.MaterialInventoryMovementRepository;
import com.lahoa.lahoa_be.service.CodeGeneratorService;
import com.lahoa.lahoa_be.service.MaterialInventoryMovementService;
import com.lahoa.lahoa_be.specification.MaterialInventoryMovementSpecification;
import com.lahoa.lahoa_be.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialInventoryMovementServiceImpl implements MaterialInventoryMovementService {

    private final MaterialInventoryMovementRepository movementRepository;
    private final PagedMapper pagedMapper;
    private final MaterialInventoryMovementMapper movementMapper;
    private final CodeGeneratorService codeService;

    @Override
    public void log(
            MaterialInventoryEntity inv,
            InventoryMovementType type,
            Integer quantity,
            Integer beforeOnHand,
            Integer beforeReserved,
            String note,
            InventoryReferenceType referenceType,
            Long referenceId,
            String referenceCode
    ){
        UserEntity user = SecurityUtils.getCurrentUser();

        MaterialInventoryMovementEntity logEntity = movementRepository.save(
            MaterialInventoryMovementEntity
                .builder()
                .code(codeService.next(CodePrefix.INMV))
                .material(inv.getMaterial())
                .warehouse(inv.getWarehouse())
                .type(type)
                .quantity(quantity)
                .beforeOnHand(beforeOnHand)
                .afterOnHand(inv.getOnHand())
                .beforeReserved(beforeReserved)
                .afterReserved(inv.getReserved())
                .note(note)
                .referenceType(referenceType)
                .referenceCode(referenceCode)
                .referenceId(referenceId)
                .build()
        );

        if(user != null) {
            logEntity.setActorId(user.getId());
            logEntity.setActorName(user.getFullName());
            logEntity.setActorEmail(user.getEmail());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MaterialInventoryMovementResponseDTO> list(MaterialInventoryMovementFilterRequestDTO filter) {
        Specification<MaterialInventoryMovementEntity> spec = MaterialInventoryMovementSpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<MaterialInventoryMovementEntity> movementPaged = movementRepository.findAll(spec, pageable);

        List<MaterialInventoryMovementResponseDTO> dtoList = movementPaged.getContent()
                .stream().map(movementMapper::toDTO).toList();

        return pagedMapper.toDTO(movementPaged, dtoList);
    }
}
