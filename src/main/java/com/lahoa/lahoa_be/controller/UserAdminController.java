package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.response.DropdownResponseDTO;
import com.lahoa.lahoa_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<DropdownResponseDTO>> searchByKeyword(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(
                userService.searchByKeyword(keyword)
        );
    }
}
