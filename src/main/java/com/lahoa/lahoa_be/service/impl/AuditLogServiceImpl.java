package com.lahoa.lahoa_be.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import com.lahoa.lahoa_be.dto.filter.AuditLogFilterDTO;
import com.lahoa.lahoa_be.dto.response.AuditLogResponseDTO;
import com.lahoa.lahoa_be.dto.response.PagedResponseDTO;
import com.lahoa.lahoa_be.entity.RoleEntity;
import com.lahoa.lahoa_be.entity.UserEntity;
import com.lahoa.lahoa_be.mapper.PagedMapper;
import com.lahoa.lahoa_be.service.AuditLogService;
import com.lahoa.lahoa_be.specification.AuditLogSpecification;
import com.lahoa.lahoa_be.util.AuditContext;
import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.entity.AuditLogEntity;
import com.lahoa.lahoa_be.repository.AuditLogRepository;
import com.lahoa.lahoa_be.util.SecurityUtils;
import com.lahoa.lahoa_be.util.TransactionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final PagedMapper pagedMapper;
    private final AuditContext auditContext;

    @Lazy
    @Autowired
    private AuditLogServiceImpl self;

    @Override
    public void log(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData,
            Object changed
    ) {

        try {
            UserEntity user = SecurityUtils.getCurrentUser();

            AuditLogEntity logEntity = AuditLogEntity.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .entityLabel(entityLabel)

                    .oldData(toJson(oldData))
                    .newData(toJson(newData))
                    .changedFields(toJson(changed))

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

                String roles = user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.joining(","));
                logEntity.setActorType(roles);
            }

            auditLogRepository.save(logEntity);

        } catch (Exception e) {
            log.error("Audit log failed", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWithContext(
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
    ) {
        try {

            AuditLogEntity logEntity = AuditLogEntity.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .entityLabel(entityLabel)

                    .oldData(toJson(oldData))
                    .newData(toJson(newData))
                    .changedFields(toJson(changed))

                    .ipAddress(ipAddress)
                    .endpoint(endpoint)
                    .method(method)
                    .userAgent(userAgent)

                    .traceId(java.util.UUID.randomUUID().toString())
                    .build();

            if (user != null) {
                logEntity.setUserId(user.getId());
                logEntity.setUserEmail(user.getEmail());
                logEntity.setUserName(user.getFullName());

                String roles = user.getRoles()
                        .stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.joining(","));

                logEntity.setActorType(roles);
            }

            auditLogRepository.save(logEntity);

        } catch (Exception e) {
            log.error("Audit log failed", e);
        }
    }

    @Override
    public void logAfterCommit(
            AuditAction action,
            AuditEntityType entityName,
            Long entityId,
            String entityLabel,
            Object oldData,
            Object newData,
            Object changed
    ) {
        UserEntity user = SecurityUtils.getCurrentUser();

        String ipAddress = auditContext.getIpAddress();
        String endpoint = auditContext.getEndpoint();
        String method = auditContext.getMethod();
        String userAgent = auditContext.getUserAgent();

        TransactionUtils.runAfterCommit(() ->
                self.logWithContext(
                        action,
                        entityName,
                        entityId,
                        entityLabel,
                        oldData,
                        newData,
                        changed,
                        user,
                        ipAddress,
                        endpoint,
                        method,
                        userAgent
                )
        );
    }

    private String toJson(Object data) {
        if (data == null) return null;

        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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
