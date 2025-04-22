package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.model.UserSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserSnapshotEntityRepository extends JpaRepository<UserSnapshotEntity, String> {
}
