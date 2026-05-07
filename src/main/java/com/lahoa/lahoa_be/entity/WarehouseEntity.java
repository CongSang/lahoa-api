package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;

@Entity
@Table(name="warehouses")
class WarehouseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String code; // HCM-01
    String name;
    String address;
}
