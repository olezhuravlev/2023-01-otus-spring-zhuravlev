package ru.otus.shared.validation;

import jakarta.validation.Constraint;
import ru.otus.shared.model.Genre;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = HasIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasId {
    String message() default "Id not specified";

    Class<?>[] groups() default {};

    Class<? extends Genre>[] payload() default {};
}
