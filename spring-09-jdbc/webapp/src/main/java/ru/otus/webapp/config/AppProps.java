package ru.otus.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties(prefix = "webapp")
public record AppProps(
        Locale locale,
        String restServerUri) {
}
