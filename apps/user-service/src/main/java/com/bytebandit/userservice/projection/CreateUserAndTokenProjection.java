package com.bytebandit.userservice.projection;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Interface makes it easier to map the result of native query
 */
public interface CreateUserAndTokenProjection {

    UUID getId();
    String getFullName();
    String getEmail();
    Boolean getVerified();
    Timestamp getCreatedAt();
}
