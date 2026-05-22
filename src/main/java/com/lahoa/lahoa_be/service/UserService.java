package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;

import java.util.List;

public interface UserService {

    List<DropdownResponseDTO> searchByKeyword(String keyword);
}
