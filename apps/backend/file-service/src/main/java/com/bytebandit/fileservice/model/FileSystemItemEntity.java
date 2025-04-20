package com.bytebandit.fileservice.model;

import com.bytebandit.fileservice.enums.UploadStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
    name = "files_system_item"
)
@EntityListeners(AuditingEntityListener.class)
public class FileSystemItemEntity {

    @Getter
    @RequiredArgsConstructor
    public enum FileSystemType {
        FILE("file"),
        FOLDER("folder");

        private final String type;
    }

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
    private UploadStatus status;

    @Column(nullable = false, updatable = false)
    private FileSystemType type;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode chunks;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private String s3Url;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private FileSystemItemEntity parent;

}
