package ru.otus.restservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties(prefix = "restservice")
public record AppProps(
        Locale locale,
        String mongodbConnectionString,
        String authorsCollectionName,
        String genresCollectionName,
        String booksCollectionName,
        String commentsCollectionName) {
}
