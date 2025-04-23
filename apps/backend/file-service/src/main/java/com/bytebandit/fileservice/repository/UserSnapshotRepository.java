package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.UserSnapshotEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSnapshotRepository extends JpaRepository<UserSnapshotEntity, UUID> {
}
