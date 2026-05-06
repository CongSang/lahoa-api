package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_slug", columnList = "slug", unique = true),
                @Index(name = "idx_product_status", columnList = "status")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE products SET status = 'DELETED' WHERE id = ?")
public class ProductEntity extends BaseEntity {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String mainImage;

    private String imagePublicId;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;

    private Integer displayOrder = 0;

    private String seoTitle;

    private String seoDescription;

    private String seoKeywords;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariantEntity> variants = new ArrayList<>();

    // options (size, color)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPropertyValueEntity> propertyValues = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategoryMappingEntity> categoryMappings = new ArrayList<>();
}