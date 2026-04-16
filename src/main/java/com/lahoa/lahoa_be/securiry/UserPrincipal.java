package com.lahoa.lahoa_be.securiry;

import com.lahoa.lahoa_be.common.enums.Status;
import com.lahoa.lahoa_be.entity.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.*;

@Data
@NullMarked
@NoArgsConstructor
public class UserPrincipal implements UserDetails, OidcUser, OAuth2User, Serializable {

    private UserEntity user;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UserEntity user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;

        Set<GrantedAuthority> auths = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                auths.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> {
                        auths.add(new SimpleGrantedAuthority(p.getName()));
                    });
                }
            });
        }
        this.authorities = auths;
    }

    public static UserPrincipal create(UserEntity user) {
        return new UserPrincipal(user, null);
    }

    public static UserPrincipal create(UserEntity user, Map<String, Object> attributes) {
        return new UserPrincipal(user, attributes);
    }

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus() == Status.ACTIVE;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getEmail());
    }

    @Override
    public Map<String, Object> getClaims() { return null; }

    @Override
    public OidcIdToken getIdToken() { return null; }

    @Override
    public OidcUserInfo getUserInfo() { return null; }
}
