package lib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for validating email format. */
@Documented
@NotNull(message = "Email field cannot be null")
@Email(message = "Invalid email format detected")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
public @interface ValidEmail {
    /** Message to be displayed when validation fail. */
    String message() default "Invalid email";

    /** Groups for validation. */
    Class<?>[] groups() default {};

    /** Payload for validation. */
    Class<? extends Payload>[] payload() default {};
}
