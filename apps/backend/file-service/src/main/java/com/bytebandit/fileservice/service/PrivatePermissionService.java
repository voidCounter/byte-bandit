package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.ItemSharePrivateRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivatePermissionService {

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
    public void givePermissionToUsers(ItemSharePrivateRequest request) {

    }

}
