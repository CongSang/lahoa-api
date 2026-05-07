package com.lahoa.lahoa_be.dto.filter;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.dto.request.PagedRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuditLogFilterDTO extends PagedRequestDTO {

    private String keyword;
    private String entityName;
    private AuditAction action;
    private Long userId;
}