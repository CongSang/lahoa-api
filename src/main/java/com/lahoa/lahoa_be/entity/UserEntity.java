package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.AuthProvider;
import com.lahoa.lahoa_be.common.enums.Role;
import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @JsonIgnore
    private String password;

    private String userImageUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Active account when login by email and password
    private String activationToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;

    @PrePersist
    public void prePersist() {
        if(this.status == null) {
            this.status = Status.INACTIVE;
        }

        if(this.role == null) {
            this.role = Role.CUSTOMER;
        }

        if(this.provider == null) {
            this.provider = AuthProvider.LOCAL;
        }
    }
}
