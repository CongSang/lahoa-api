package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "stocktakes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

    @Column(length = 1000)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @OneToMany(
            mappedBy = "stocktake",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StocktakeDetailEntity> details;
}
