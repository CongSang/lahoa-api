package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.WarehouseFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.WarehouseRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.WarehouseResponseDTO;

import java.util.List;

public interface WarehouseService {

    PagedResponseDTO<WarehouseResponseDTO> list(WarehouseFilterRequestDTO filter);

    List<DropdownResponseDTO> getDropdown();

    WarehouseResponseDTO create(WarehouseRequestDTO req);

    WarehouseResponseDTO update(Long id, WarehouseRequestDTO req);

    WarehouseResponseDTO updateStatus(Long id);

    void delete(Long id);

    WarehouseResponseDTO restore(Long id);
}
