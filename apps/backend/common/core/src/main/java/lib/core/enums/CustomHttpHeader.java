package lib.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CustomHttpHeader {
    USER_ID("X-User-Id");

    private String value;
}
