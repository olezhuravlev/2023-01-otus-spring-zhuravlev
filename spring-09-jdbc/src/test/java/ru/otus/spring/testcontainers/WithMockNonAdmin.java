package ru.otus.spring.testcontainers;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "non-admin", roles = {"COMMENTER", "READER"})
public @interface WithMockNonAdmin {
}
