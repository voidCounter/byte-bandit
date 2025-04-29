package lib.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@MappedSuperclass
@Data
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserEntityTemplate implements UserDetails, Principal {
    
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