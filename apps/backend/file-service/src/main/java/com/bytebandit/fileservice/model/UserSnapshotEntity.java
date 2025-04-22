package com.bytebandit.fileservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_snapshot")
public class UserSnapshotEntity {
    
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "email", nullable = false)
    private String email;
}