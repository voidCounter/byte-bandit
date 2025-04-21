package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.FileSystemItemEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSystemItemRepository extends JpaRepository<FileSystemItemEntity, UUID> { }
