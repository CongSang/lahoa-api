package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "material_receipts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialReceiptEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    private String supplier;

    private BigDecimal totalCost;

    private String note;

    private Long createdBy;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<MaterialReceiptDetailEntity> details;
}
