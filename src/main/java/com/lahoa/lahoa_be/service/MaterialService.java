package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.MaterialFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;

public interface MaterialService {

    PagedResponseDTO<MaterialResponseDTO> list(MaterialFilterRequestDTO filter);

    MaterialResponseDTO create(MaterialRequestDTO req);

    MaterialResponseDTO update(Long id, MaterialRequestDTO req);

    MaterialResponseDTO getById(Long id);

    void delete(Long id);

    MaterialResponseDTO restore(Long id);

    MaterialResponseDTO updateStatus(Long id);
}
