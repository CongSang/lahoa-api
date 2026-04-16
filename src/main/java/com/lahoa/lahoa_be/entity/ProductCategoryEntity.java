package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_categories", indexes = {
        @Index(name = "idx_category_slug", columnList = "slug"),
        @Index(name = "idx_category_parent_id", columnList = "parent_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String imageUrl;

    @Column(length = 500)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductCategoryEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
    private List<ProductCategoryEntity> children = new ArrayList<>();

//    @OneToMany(mappedBy = "category")
//    private List<Product> products;
}
