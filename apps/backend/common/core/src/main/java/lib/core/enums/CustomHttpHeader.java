package lib.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomHttpHeader {
    USER_ID("X-User-Id");

    private String value;
}
