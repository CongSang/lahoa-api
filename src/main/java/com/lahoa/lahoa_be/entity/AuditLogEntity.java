package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.AuditAction;
import com.lahoa.lahoa_be.common.enums.AuditEntityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_entity", columnList = "entity_name, entity_id"),
                @Index(name = "idx_audit_user", columnList = "user_id"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_created_at", columnList = "created_at"),
                @Index(name = "idx_audit_trace_id", columnList = "trace_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * PRODUCT
     * ORDER
     * CATEGORY
     * USER
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private AuditEntityType entityName;

    /**
     * ID của entity bị tác động
     */
    @Column(nullable = false)
    private Long entityId;

    /**
     * CREATE
     * UPDATE
     * DELETE
     * RESTORE
     * LOGIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuditAction action;

    /**
     * Tên hiển thị để admin đọc dễ hơn
     * Ví dụ:
     * "Hoa hồng đỏ"
     */
    @Column(length = 255)
    private String entityLabel;

    /**
     * JSON trước khi thay đổi
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String oldData;

    /**
     * JSON sau khi thay đổi
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String newData;

    /**
     * Chỉ lưu field thay đổi
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String changedFields;

    /**
     * Người thao tác
     */
    private Long userId;

    /**
     * Email user thao tác
     * để query nhanh không cần join user
     */
    @Column(length = 255)
    private String userEmail;

    @Column(length = 255)
    private String userName;

    /**
     * ADMIN / STAFF / SYSTEM
     */
    @Column(length = 50)
    private String actorType;

    /**
     * IP address
     */
    @Column(length = 100)
    private String ipAddress;

    /**
     * Chrome / Safari / Mobile...
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * request id để trace log
     */
    @Column(length = 100)
    private String traceId;

    /**
     * API endpoint
     */
    @Column(length = 500)
    private String endpoint;

    /**
     * HTTP method
     */
    @Column(length = 20)
    private String method;

    /**
     * Có rollback được không
     */
    private Boolean reversible;

    /**
     * Optional note
     */
    @Column(length = 1000)
    private String note;
}
