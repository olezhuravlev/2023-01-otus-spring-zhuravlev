package ru.otus.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HasIdValidator implements ConstraintValidator<HasId, Object> {

    @Override
    public void initialize(HasId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        if (value instanceof String stringValue) {
            return !stringValue.isBlank();
        }

        return true;
    }
}
