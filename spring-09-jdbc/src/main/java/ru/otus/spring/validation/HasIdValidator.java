package ru.otus.spring.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Genre;

public class HasIdValidator implements ConstraintValidator<HasId, Object> {

    @Override
    public void initialize(HasId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        Long id = null;
        if (value instanceof Author) {
            id = ((Author) value).getId();
        } else if (value instanceof Genre) {
            id = ((Genre) value).getId();
        }

        return id != null && id > 0;
    }
}
