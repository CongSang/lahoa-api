package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.filter.StocktakeFilterRequestDTO;
import com.lahoa.lahoa_be.dto.request.CreateStocktakeRequestDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeMaterialResponseDTO;
import com.lahoa.lahoa_be.dto.response.StocktakeResponseDTO;

import java.util.List;

public interface StocktakeService {

    StocktakeResponseDTO create(
            CreateStocktakeRequestDTO req
    );

    StocktakeResponseDTO detail(
            Long id
    );

    PagedResponseDTO<StocktakeResponseDTO> list(
            StocktakeFilterRequestDTO req
    );

    List<StocktakeMaterialResponseDTO> getMaterialsForStocktake(
            Long warehouseId
    );
}
