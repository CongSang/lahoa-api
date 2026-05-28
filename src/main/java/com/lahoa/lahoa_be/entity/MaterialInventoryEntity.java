package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "material_inventories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "material_id",
                                "warehouse_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialInventoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialEntity material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @Column(nullable = false)
    private Integer onHand;

    @Column(nullable = false)
    private Integer reserved;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Version
    private Long version;

    public Integer getAvailable() {
        return onHand - reserved;
    }
}
