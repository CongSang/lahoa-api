package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.MaterialUnit;
import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE materials SET status = 'DELETED' WHERE id = ?")
public class MaterialEntity extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private MaterialCategoryEntity category;

    @Column(
            nullable = false,
            unique = true
    )
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MaterialUnit unit;

    private String thumbnail;
    private String thumbnailPublicId;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "material")
    private List<MaterialInventoryEntity> inventories;
}
