package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;

import java.util.List;

public interface MaterialService {

    MaterialResponseDTO create(MaterialRequestDTO req);

    MaterialResponseDTO update(Long id, MaterialRequestDTO req);

    void delete(Long id);

    MaterialResponseDTO restore(Long id);

    MaterialResponseDTO updateStatus(Long id);

    List<DropdownResponseDTO> getDropdown();
}
