package com.lahoa.lahoa_be.dto.response;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponseDTO {

    private Long id;

    private AuditEntityType entityName;
    private Long entityId;
    private String entityLabel;

    private AuditAction action;

    private String oldData;
    private String newData;
    private String changedFields;

    private Long userId;
    private String userEmail;

    private String ipAddress;
    private String endpoint;
    private String method;

    private String traceId;

    private LocalDateTime createAt;
}
