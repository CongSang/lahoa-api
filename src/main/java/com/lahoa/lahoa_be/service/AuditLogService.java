package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.dto.filter.AuditLogFilterDTO;
import com.lahoa.lahoa_be.dto.response.AuditLogResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.UserEntity;

public interface AuditLogService {

    void log(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData,
            Object changed
    );

    void logWithContext(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData,
            Object changed,
            UserEntity user,
            String ipAddress,
            String endpoint,
            String method,
            String userAgent
    );

    void logAfterCommit(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData,
            Object changed
    );

    PagedResponseDTO<AuditLogResponseDTO> list(AuditLogFilterDTO filter);
}
