package com.bytebandit.fileservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = { ListSizeChecker.class })
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ListSizeEqual {

    /**
     * The message to be returned when the validation fails.
     */
    String message() default "The sizes of the lists must be the same.";

    /**
     * The groups the constraint belongs to.
     */
    Class<?>[] groups() default {};

    /**
     * Additional data to be carried with the annotation.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The names of the fields to be compared.
     */
    String list1FieldName();

    /**
     * The names of the fields to be compared.
     */
    String list2FieldName();
}
