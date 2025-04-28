package com.bytebandit.fileservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Does not check if the Id is <b>blank</b>.
 */
@Documented
@Constraint(validatedBy = IdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {

    /**
     *  Default meesage.
     */
    String message() default "Invalid UUID format";

    /**
     * Groups it belongs to.
     */
    Class<?>[] groups() default {};

    /**
     * Payload.
     */
    Class<? extends Payload>[] payload() default {};
}
