package lib.core.dto.response;

import lib.core.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldValidationError {
    private String field;
    private ErrorCode code;
    private String message;
}