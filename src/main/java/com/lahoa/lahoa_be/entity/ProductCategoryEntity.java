package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_category_slug", columnList = "slug"),
                @Index(name = "idx_category_parent_id", columnList = "parent_id"),
                @Index(name = "idx_category_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE categories SET status = 'DELETED' WHERE id = ?")
@Builder
public class ProductCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String path;

    private String imageUrl;

    private String imagePublicId;

    @Column(length = 500)
    private String description;

    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private String seoTitle;

    private String seoDescription;

    private String seoKeywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductCategoryEntity parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("displayOrder ASC")
    private List<ProductCategoryEntity> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<ProductCategoryMappingEntity> productMappings = new ArrayList<>();
}
