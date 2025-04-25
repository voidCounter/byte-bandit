package com.bytebandit.fileservice.projection;

/**
 * Interface makes it easier to map the result of native query.
 */
public interface ShareItemPrivateProjection {

    String[] getPermissionForEachUser();
}
