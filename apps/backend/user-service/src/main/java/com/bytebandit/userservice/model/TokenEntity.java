package com.bytebandit.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lib.user.model.TokenEntityTemplate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tokens")
@Getter
@Setter
public class TokenEntity extends TokenEntityTemplate {

    /**
     * Many-to-one relationship with the UserEntity. Fetches the user lazily and joins on the
     * 'user_id' column.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}