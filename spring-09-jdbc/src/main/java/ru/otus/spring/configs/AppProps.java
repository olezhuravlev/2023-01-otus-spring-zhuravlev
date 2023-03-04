package ru.otus.spring.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties(prefix = "application")
public record AppProps(
        Locale locale,
        String dbLikeTemplate) {
}
