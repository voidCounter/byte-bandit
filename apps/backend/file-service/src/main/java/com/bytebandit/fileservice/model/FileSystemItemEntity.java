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

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private UUID owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UploadStatus status;

    @Column(nullable = false, updatable = false)
    private FileSystemItemType type;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode chunks;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private String s3Url;

    @ManyToOne(
        cascade = CascadeType.ALL,
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
}
