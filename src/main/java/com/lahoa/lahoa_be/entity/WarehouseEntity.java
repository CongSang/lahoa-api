package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name="warehouses")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@SQLDelete(sql = "UPDATE warehouses SET status = 'DELETED' WHERE id = ?")
public class WarehouseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String code; // HCM-01
    String name;
    String address;

    @Enumerated(EnumType.STRING)
    Status status;
}
