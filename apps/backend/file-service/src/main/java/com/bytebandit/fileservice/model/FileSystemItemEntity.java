package com.bytebandit.fileservice.model;

import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "file_system_items"
)
@EntityListeners(AuditingEntityListener.class)
public class FileSystemItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    private Long size;
    
    private String mimeType;
    
    @Column(nullable = false)
    private UUID owner;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UploadStatus status;
    
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private FileSystemItemType type;
    
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode chunks;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    
    @UpdateTimestamp
    private Timestamp updatedAt;
    
    private String s3Url;
    
    @ManyToOne(
        cascade = CascadeType.MERGE,
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "parent_id",
        referencedColumnName = "id"
    )
    private FileSystemItemEntity parent;
    
    @OneToMany(
        mappedBy = "item",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<SharedItemsPrivateEntity> sharedItems;
    
    @OneToOne(
        mappedBy = "item",
        orphanRemoval = true
    )
    private SharedItemsPublicEntity sharedItem;
    
    @OneToOne(
        mappedBy = "item",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private ItemsStarredEntity starredItem;
    
    @OneToOne(
        mappedBy = "item",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private ItemViewedEntity viewedItem;
    
    @PrePersist
    @PreUpdate
    private void validateItemTypeConstraints() {
        boolean isFolder = type == FileSystemItemType.FOLDER;
        boolean isFile = type == FileSystemItemType.FILE;
        if ((isFolder) && (s3Url != null || size != null || mimeType != null)) {
            throw new IllegalStateException("Folder cannot have an S3 URL or size or mimeType");
        }
        if ((isFile) && (s3Url == null || size == null || mimeType == null)) {
            throw new IllegalStateException("File must have an S3 URL mimeType and size");
        }
    }
}
