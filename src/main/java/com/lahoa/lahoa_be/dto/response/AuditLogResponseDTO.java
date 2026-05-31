package com.lahoa.lahoa_be.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponseDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private AuditEntityType entityName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long entityId;
    private String entityLabel;

    private AuditAction action;

    private String oldData;
    private String newData;
    private String changedFields;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String userEmail;

    private String ipAddress;
    private String endpoint;
    private String method;

    private String traceId;

    private LocalDateTime createAt;
}
