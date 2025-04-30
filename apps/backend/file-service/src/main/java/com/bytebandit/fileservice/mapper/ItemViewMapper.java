package com.bytebandit.fileservice.mapper;

import com.bytebandit.fileservice.dto.ChildResponse;
import com.bytebandit.fileservice.dto.ItemViewResponse;
import com.bytebandit.fileservice.projection.ItemViewProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemViewMapper {


    private ItemViewMapper() {

    }

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

        ItemViewResponse response = new ItemViewResponse();

        String rawChildrenJson = projection.getOutputChildren();
        if (rawChildrenJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<ChildResponse> children = objectMapper.readValue(
                    rawChildrenJson,
                    new TypeReference<>() {}
                );
                response.setChildren(children);
            } catch (JsonProcessingException e) {
                log.info("Failed to process child response {}", e.getMessage());
            }
        }

        log.info("Child items after mapping: {}", response.getChildren());

        response.setItemId(projection.getOutputItemId());
        response.setCreatedAt(projection.getOutputCreatedAt());
        response.setUpdatedAt(projection.getOutputUpdatedAt());
        response.setOwnerEmail(projection.getOutputOwnerEmail());
        response.setSharedByEmail(projection.getOutputSharedByEmail());
        response.setItemType(projection.getOutputItemType());
        response.setIsItemPasswordProtected(projection.getOutputIsItemPasswordProtected());
        response.setName(projection.getOutputName());
        response.setS3Url(projection.getOutputS3Url());
        response.setMimeType(projection.getOutputMimeType());
        response.setSize(projection.getOutputSize());
        response.setIsStarred(projection.getOutputIsStarred());
        response.setParentId(projection.getOutputParentId());
        response.setPermission(projection.getOutputPermission());

        return response;
    }
}
