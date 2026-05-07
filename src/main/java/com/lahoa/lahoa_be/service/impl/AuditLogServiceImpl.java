package com.lahoa.lahoa_be.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.dto.filter.AuditLogFilterDTO;
import com.lahoa.lahoa_be.dto.response.AuditLogResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.service.AuditLogService;
import com.lahoa.lahoa_be.specification.AuditLogSpecification;
import com.lahoa.lahoa_be.util.AuditContext;
import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.entity.AuditLogEntity;
import com.lahoa.lahoa_be.repository.AuditLogRepository;
import com.lahoa.lahoa_be.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final PagedMapper pagedMapper;
    private final AuditContext auditContext;

    @Override
    public void log(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData
    ) {

        try {
            UserEntity user = SecurityUtils.getCurrentUser();

            String oldJson = oldData != null
                    ? objectMapper.writeValueAsString(oldData)
                    : null;

            String newJson = newData != null
                    ? objectMapper.writeValueAsString(newData)
                    : null;

            AuditLogEntity logEntity = AuditLogEntity.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .entityLabel(entityLabel)

                    .oldData(oldJson)
                    .newData(newJson)

                    .ipAddress(auditContext.getIpAddress())
                    .endpoint(auditContext.getEndpoint())
                    .method(auditContext.getMethod())
                    .userAgent(auditContext.getUserAgent())

                    .traceId(java.util.UUID.randomUUID().toString())
                    .build();

            if(user != null) {
                logEntity.setUserId(user.getId());
                logEntity.setUserEmail(user.getEmail());
                logEntity.setUserName(user.getFullName());
            }

            auditLogRepository.save(logEntity);

        } catch (Exception e) {
            log.error("Audit log failed", e);
        }
    }

    public PagedResponseDTO<AuditLogResponseDTO> list(AuditLogFilterDTO filter) {
        Specification<AuditLogEntity> spec = AuditLogSpecification.filter(filter);

        Sort sort = filter.getSortOrder().equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(filter.getSortField()).ascending()
                : Sort.by(filter.getSortField()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort
        );

        Page<AuditLogEntity> auditPaged = auditLogRepository.findAll(spec, pageable);

        List<AuditLogResponseDTO> dtoList = auditPaged.getContent().stream()
                .map(log -> {
                    return AuditLogResponseDTO.builder()
                            .id(log.getId())
                            .entityName(log.getEntityName())
                            .entityId(log.getEntityId())
                            .entityLabel(log.getEntityLabel())
                            .action(log.getAction())
                            .oldData(log.getOldData())
                            .newData(log.getNewData())
                            .changedFields(log.getChangedFields())
                            .userId(log.getUserId())
                            .userEmail(log.getUserEmail())
                            .ipAddress(log.getIpAddress())
                            .endpoint(log.getEndpoint())
                            .method(log.getMethod())
                            .traceId(log.getTraceId())
                            .createAt(log.getCreatedAt())
                            .build();
                })
                .toList();

        return pagedMapper.toDTO(auditPaged, dtoList);
    }
}
