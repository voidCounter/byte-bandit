package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.ItemViewRequest;
import com.bytebandit.fileservice.dto.ItemViewResponse;
import com.bytebandit.fileservice.exception.ItemViewException;
import com.bytebandit.fileservice.mapper.ItemViewMapper;
import com.bytebandit.fileservice.projection.ItemViewProjection;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemViewService {

    private final FileSystemItemRepository fileSystemItemRepository;
    private final RoleBasedAccessControlService roleBasedAccessControlService;

    /**
     * Retrieves the view of an item based on the provided request, user ID, and permission.
     *
     * @param itemViewRequest The request containing the item ID and optional password.
     * @param userId The ID of the user making the request.
     * @return An ItemViewResponse containing the details of the item.
     * @throws ItemViewException if the item is not found or if there are any issues retrieving it.
     */
    public ItemViewResponse getItemView(
        ItemViewRequest itemViewRequest,
        UUID userId
    ) {
        final String permission = roleBasedAccessControlService.getPermission(
            itemViewRequest.getItemId(),
            userId
        ).toUpperCase();
        if (permission.equals("NO_ACCESS")) {
            throw new ItemViewException("You do not have access to this item.");
        }
        ItemViewProjection response = fileSystemItemRepository.viewItems(
            UUID.fromString(itemViewRequest.getItemId()),
            userId,
            permission
        ).orElseThrow(() -> new ItemViewException("Item not found"));

        return ItemViewMapper.mapToResponse(response);
    }
}
