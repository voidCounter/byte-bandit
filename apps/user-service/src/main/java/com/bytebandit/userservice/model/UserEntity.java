package com.bytebandit.userservice.model;

import com.bytebandit.userservice.annotation.ValidEmail;
import com.bytebandit.userservice.annotation.ValidPassword;
import com.bytebandit.userservice.annotation.ValidUsername;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true)
    @ValidEmail
    private String email;

    @Column(name = "password", length = 72)
    @ValidPassword
    private String password;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "username")
    @ValidUsername
    private String username;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
}
