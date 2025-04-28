package lib.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private EnumValidator annotation;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Object[] enumValues = annotation.enumClass().getEnumConstants();
        return Arrays.stream(enumValues)
            .anyMatch(e -> e.toString().equalsIgnoreCase(value));
    }
}
