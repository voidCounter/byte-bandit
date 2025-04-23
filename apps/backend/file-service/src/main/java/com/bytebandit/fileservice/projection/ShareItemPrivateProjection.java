package com.bytebandit.fileservice.projection;

import java.util.List;

/**
 * Interface makes it easier to map the result of native query.
 */
public interface ShareItemPrivateProjection {

    List<String> getPermissionForEachUser();
}
