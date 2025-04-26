package lib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorImpl.class)
@Documented
public @interface EnumValidator {

    /**
     * The enum class to validate against.
     *
     * @return the enum class
     */
    Class<?> enumClass();

    /**
     * The message to be returned if the validation fails.
     *
     * @return the error message
     */
    String message() default "must be any of enum {enumClass}";

    /**
     * The groups the constraint belongs to.
     *
     * @return the groups
     */
    Class<?>[] groups() default {};

    /**
     * The payload to be carried with the annotation.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};
}
