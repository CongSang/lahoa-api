package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.repository.UserRepository;
import com.lahoa.lahoa_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<DropdownResponseDTO> searchByKeyword(
            String keyword
    ) {
        return userRepository
                .findByKeyword(keyword == null ? "" : keyword)
                .stream()
                .map(user -> {
                    return DropdownResponseDTO.builder()
                            .id(user.getId())
                            .value(user.getId().toString())
                            .label(user.getFullName()
                                    + " ("
                                    + user.getEmail()
                                    + ")")
                            .build();
                })
                .toList();
    }
}
