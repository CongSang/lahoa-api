package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.MaterialReceiptFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.MaterialImportRequestDTO;
import com.lahoa.lahoa_be.dto.response.MaterialReceiptResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;

public interface MaterialReceiptService {

    MaterialReceiptResponseDTO create(MaterialImportRequestDTO req);

    MaterialReceiptResponseDTO getById(Long id);

    PagedResponseDTO<MaterialReceiptResponseDTO> list(MaterialReceiptFilterRequestDTO filter);
}
