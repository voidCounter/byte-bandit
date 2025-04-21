package com.bytebandit.fileservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//@Entity
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(
//    name = "item_views"
//)
//@EntityListeners(AuditingEntityListener.class)
public class ItemViewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


}
