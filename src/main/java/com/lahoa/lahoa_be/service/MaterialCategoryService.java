package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.MaterialCategoryFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialCategoryRequestDTO;
import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.dto.response.MaterialCategoryResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;

import java.util.List;

public interface MaterialCategoryService {

    PagedResponseDTO<MaterialCategoryResponseDTO> list(MaterialCategoryFilterRequestDTO filter);

    MaterialCategoryResponseDTO getById(Long id);

    List<DropdownResponseDTO> getDropdown();

    MaterialCategoryResponseDTO create(MaterialCategoryRequestDTO req);

    MaterialCategoryResponseDTO update(Long id, MaterialCategoryRequestDTO req);

    void delete(Long id);

    MaterialCategoryResponseDTO restore(Long id);

    MaterialCategoryResponseDTO updateStatus(Long id);
}
