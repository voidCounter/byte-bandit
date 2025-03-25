package com.bytebandit.userservice.model;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails, Principal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password_hash", length = 72)
    private String passwordHash;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "name")
    private String fullName;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    /**
     * One-to-many relationship with TokenEntity.
     * cascade = CascadeType.ALL - This means that any operation (persist, merge, remove, refresh, detach)
     * performed on the UserEntity will also be applied to the associated TokenEntity instances.
     * orphanRemoval = true - This means that if a TokenEntity instance is removed from the UserEntity's tokens collection,
     * Hashset provides efficient operations for adding, removing, and checking for the presence of elements.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TokenEntity> tokens = new HashSet<>();

    @Override
    public String getName() {
        return this.email;
    } // take note here

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // for now we don't have any roles
    }

    @Override
    public boolean isEnabled() {
        return this.verified;
    }
}
