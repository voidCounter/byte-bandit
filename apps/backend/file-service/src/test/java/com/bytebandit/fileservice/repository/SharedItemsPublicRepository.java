package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.SharedItemsPublicEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedItemsPublicRepository extends
    JpaRepository<SharedItemsPublicEntity, UUID> { }
