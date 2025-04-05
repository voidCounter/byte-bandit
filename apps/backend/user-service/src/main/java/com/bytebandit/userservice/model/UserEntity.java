package com.bytebandit.userservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lib.user.model.UserEntityTemplate;

@Entity
@Table(name = "users")
public class UserEntity extends UserEntityTemplate {

    /**
     * One-to-many relationship with TokenEntity. cascade = CascadeType.ALL - This means that any
     * operation (persist, merge, remove, refresh, detach) performed on the UserEntity will also be
     * applied to the associated TokenEntity instances. orphanRemoval = true - This means that if a
     * TokenEntity instance is removed from the UserEntity's tokens collection, Hashset provides
     * efficient operations for adding, removing, and checking for the presence of elements.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TokenEntity> tokens = new HashSet<>();

}
