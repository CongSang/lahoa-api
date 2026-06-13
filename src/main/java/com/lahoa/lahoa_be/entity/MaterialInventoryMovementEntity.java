package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.InventoryMovementType;
import com.lahoa.lahoa_be.common.enums.InventoryReferenceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material_inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialInventoryMovementEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    private MaterialEntity material;

    @ManyToOne(fetch = FetchType.LAZY)
    private WarehouseEntity warehouse;

    @Enumerated(EnumType.STRING)
    private InventoryMovementType type;

    private Integer quantity;

    private Integer beforeOnHand;

    private Integer afterOnHand;

    private Integer beforeReserved;

    private Integer afterReserved;

    private String note;

    private Long actorId;
    private String actorName;
    private String actorEmail;

    @Enumerated(EnumType.STRING)
    private InventoryReferenceType referenceType;
    private Long referenceId;
    private String referenceCode;
}
