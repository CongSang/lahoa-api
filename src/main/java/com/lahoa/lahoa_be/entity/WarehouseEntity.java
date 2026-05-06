package com.lahoa.lahoa_be.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="warehouses")
class WarehouseEntity {

    @Id
    @GeneratedValue
    Long id;

    String code; // HCM-01
    String name;
    String address;
}
