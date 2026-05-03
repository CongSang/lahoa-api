package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "properties",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_property_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String code; // flower_type, style, size

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "is_filterable", nullable = false)
    private boolean filterable;

    @Column(name = "is_selectable", nullable = false)
    private boolean selectable;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PropertyValueEntity> values = new HashSet<>();
}
