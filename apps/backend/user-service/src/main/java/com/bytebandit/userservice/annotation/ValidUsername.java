package com.bytebandit.userservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for validating username format. */
@Documented
@NotNull(message = "Username field cannot be null")
@Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
public @interface ValidUsername {
    /** Message to be displayed when validation fail. */
    String message() default "Invalid username";

    /** Groups for validation. */
    Class<?>[] groups() default {};

    /** Payload for validation. */
    Class<? extends Payload>[] payload() default {};
}
