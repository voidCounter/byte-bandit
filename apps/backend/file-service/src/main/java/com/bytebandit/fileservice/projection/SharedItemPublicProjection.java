package com.bytebandit.fileservice.projection;

import java.util.UUID;

public interface SharedItemPublicProjection {
    UUID getPublicLinkId();
    
    String getStatus();
}
