package com.bytebandit.fileservice.mapper;

import com.bytebandit.fileservice.dto.ChildResponse;
import com.bytebandit.fileservice.dto.ItemViewResponse;
import com.bytebandit.fileservice.projection.ChildProjection;
import com.bytebandit.fileservice.projection.ItemViewProjection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemViewMapper {

    /**
     * Maps an ItemViewProjection to an ItemViewResponse.
     *
     * @param projection the ItemViewProjection to map
     * @return the mapped ItemViewResponse
     */
    public static ItemViewResponse mapToResponse(ItemViewProjection projection) {
        if (projection == null) {
            return null;
        }

        List<ChildResponse> children = null;
        if (projection.getOutputChildren() != null) {
            children = projection.getOutputChildren()
                .stream()
                .map(ItemViewMapper::mapChild)
                .collect(Collectors.toList());
        }

        ItemViewResponse response = new ItemViewResponse();
        response.setItemId(projection.getOutputItemId());
        response.setCreatedAt(projection.getOutputCreatedAt());
        response.setUpdatedAt(projection.getOutputUpdatedAt());
        response.setOwnerEmail(projection.getOutputOwnerEmail());
        response.setSharedByEmail(projection.getOutputSharedByEmail());
        response.setItemType(projection.getOutputItemType());
        response.setIsItemPasswordProtected(projection.getOutputIsItemPasswordProtected());
        response.setName(projection.getOutputName());
        response.setS3Url(projection.getOutputS3url());
        response.setMimeType(projection.getOutputMimeType());
        response.setIsStarred(projection.getOutputIsStarred());
        response.setParentId(projection.getOutputParentId());
        response.setPermission(projection.getOutputPermission());
        response.setChildren(children);

        return response;
    }

    private static ChildResponse mapChild(ChildProjection childProjection) {
        ChildResponse child = new ChildResponse();
        child.setItemId(childProjection.getItemId());
        child.setCreatedAt(childProjection.getCreatedAt());
        child.setUpdatedAt(childProjection.getUpdatedAt());
        child.setOwnerEmail(childProjection.getOwnerEmail());
        child.setSharedByEmail(childProjection.getSharedByEmail());
        child.setItemType(childProjection.getItemType());
        child.setIsItemPasswordProtected(childProjection.getIsItemPasswordProtected());
        child.setName(childProjection.getName());
        child.setS3Url(childProjection.getS3Url());
        child.setMimeType(childProjection.getMimeType());
        child.setIsStarred(childProjection.getIsStarred());
        child.setParentId(childProjection.getParentId());
        child.setPermission(childProjection.getPermission());
        return child;
    }
}
