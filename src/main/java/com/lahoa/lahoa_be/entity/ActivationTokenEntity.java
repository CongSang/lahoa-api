package com.lahoa.lahoa_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "activation_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Instant expiryDate;

    private boolean used;

    private Instant lastSentAt;
}
