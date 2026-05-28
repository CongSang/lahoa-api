package com.lahoa.lahoa_be.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "code_sequences")
@Getter
@Setter
public class CodeSequenceEntity {

    @Id
    private String prefix;

    private Long value;
}
