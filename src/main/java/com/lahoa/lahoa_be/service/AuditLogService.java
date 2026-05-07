package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;

public interface AuditLogService {

    void log(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData
    );
}
