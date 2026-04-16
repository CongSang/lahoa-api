package com.lahoa.lahoa_be.entity;

import com.lahoa.lahoa_be.common.enums.AuthProvider;
import com.lahoa.lahoa_be.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="users")
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;

    @PrePersist
    public void prePersist() {
        if(this.status == null) {
            this.status = Status.INACTIVE;
        }

        if(this.provider == null) {
            this.provider = AuthProvider.LOCAL;
        }
    }
}
