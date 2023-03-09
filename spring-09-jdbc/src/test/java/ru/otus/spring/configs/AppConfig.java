package ru.otus.spring.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

@TestConfiguration
public class AppConfig {
    @Bean
    public MessageSource messageSource() {
        return new AbstractMessageSource() {
            @Override
            protected MessageFormat resolveCode(String code, Locale locale) {
                return new MessageFormat("");
            }
        };
    }
}
