package lib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for validating password format.
 */
@Documented
@NotNull(message = "Password field cannot be null")
@Pattern(
    regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
    message =
        "Password must be at least 8 characters long and contain at least one digit, one uppercase "
            + "letter, one lowercase letter, and one special character"
)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
public @interface ValidPassword {
    /** Message to be displayed when validation fail. */
    String message() default "Invalid password";

    /** Groups for validation. */
    Class<?>[] groups() default {};

    /** Payload for validation. */
    Class<? extends Payload>[] payload() default {};
}