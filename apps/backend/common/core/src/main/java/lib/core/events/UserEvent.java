package lib.core.events;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lib.core.enums.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Represents an event triggered by a user action, containing user details "
    + "and the specific action performed.")
@Data
@Builder
@AllArgsConstructor
public class UserEvent {
    private UUID userId;
    private String email;
    private UserAction action;
    
    public UserEvent() {
        // Default constructor for deserialization
    }
}

