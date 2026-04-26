package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(
        name = "property_values",
        indexes = {
                @Index(name = "idx_prop_val_property", columnList = "property_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_property_value_unique",
                        columnNames = {"property_id", "value"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id")
    private PropertyEntity property;

    @Column(nullable = false, length = 100)
    private String value; // hoa_hong, bo, S

    @Column(nullable = false, length = 150)
    private String label;
}
