package ru.otus.spring.validation;

import jakarta.validation.Constraint;
import ru.otus.spring.model.Genre;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HasIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasId {
    String message() default "Id not specified";
    Class<?>[] groups() default {};
    Class<? extends Genre>[] payload() default {};
}
