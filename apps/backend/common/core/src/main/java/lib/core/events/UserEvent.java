package lib.core.events;

import java.util.UUID;
import lib.core.enums.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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

