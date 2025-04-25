package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.ItemSharePrivateRequest;
import com.bytebandit.fileservice.dto.ItemSharePrivateResponse;
import com.bytebandit.fileservice.projection.ShareItemPrivateProjection;
import com.bytebandit.fileservice.repository.SharedItemsPrivateRepository;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivatePermissionService {

    private final SharedItemsPrivateRepository sharedItemsPrivateRepository;
    private final TransactionTemplate transactionTemplate;


    /**
     * Implementations.
     * <ol>
     *     <li>Check if itemId's owner is sharedBy</li>
     *     <ul>
     *         <li>if yes, the go ahead and allow user to give permission.</li>
     *         <li>if no, then, </li>
     *         <ol>
     *              <li>if user is given permission to parent chain with permission >= new
     *                  permission,
     *              </li>
     *              <ul>
     *                  <li>if yes, then go ahead and allow user to give permission</li>
     *                  <li>if no, then throw NotEnoughPermissionException</li>
     *              </ul>
     *         </ol>
     *     </ul>
     * </ol>
    */
    public ItemSharePrivateResponse givePermissionToUsers(ItemSharePrivateRequest request) {
        String[] sharedTo = request.getSharedTo().toArray(new String[0]);
        String[] permissions = request.getPermissions().toArray(new String[0]);
        String[] sharedPermissions = transactionTemplate.execute(result -> {

                log.info("sharedTo: {}", (Object) sharedTo);
                log.info("permissions: {}", (Object) permissions);

                return sharedItemsPrivateRepository.shareItemPrivate(
                    UUID.fromString(request.getItemId()),
                    request.getSharedBy(),
                    sharedTo,
                    permissions
                );
            }
        );
        if (sharedPermissions == null) {
            log.error("Failed to share item with id: {}", request.getItemId());
            throw new IllegalStateException("Failed to share item");
        }
        log.info("Item with id: {} shared successfully", request.getItemId());
        ItemSharePrivateResponse response = ItemSharePrivateResponse.builder()
            .permissionForEachUser(Arrays.asList(sharedPermissions))
            .build();
        log.info("Private share response: {}", response.getPermissionForEachUser());
        return response;
    }

}
