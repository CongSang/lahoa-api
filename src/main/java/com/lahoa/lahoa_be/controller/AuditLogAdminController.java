package com.lahoa.lahoa_be.controller;

import com.lahoa.lahoa_be.dto.filter.AuditLogFilterDTO;
import com.lahoa.lahoa_be.dto.response.AuditLogResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogAdminController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<AuditLogResponseDTO>> list(
            AuditLogFilterDTO filter) {
        return ResponseEntity.ok(auditLogService.list(filter));
    }
}
