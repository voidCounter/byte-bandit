package com.bytebandit.fileservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

public class ListSizeChecker implements ConstraintValidator<ListSizeEqual, Object> {

    private String list1FieldName;
    private String list2FieldName;

    @Override
    public void initialize(ListSizeEqual constraintAnnotation) {
        this.list1FieldName = constraintAnnotation.list1FieldName();
        this.list2FieldName = constraintAnnotation.list2FieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object firstList = null;
            Object secondList = null;

            for (PropertyDescriptor pd : Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors()) {
                if (pd.getName().equals(list1FieldName)) {
                    firstList = pd.getReadMethod().invoke(value);
                } else if (pd.getName().equals(list2FieldName)) {
                    secondList = pd.getReadMethod().invoke(value);
                }
            }

            if (firstList instanceof List<?> && secondList instanceof List<?>) {
                return ((List<?>) firstList).size() == ((List<?>) secondList).size();
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
