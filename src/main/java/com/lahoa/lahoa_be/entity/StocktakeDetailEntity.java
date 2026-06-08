package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocktake_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakeDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stocktake_id")
    private StocktakeEntity stocktake;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    private Integer systemQty;

    private Integer actualQty;

    private Integer difference;
}
