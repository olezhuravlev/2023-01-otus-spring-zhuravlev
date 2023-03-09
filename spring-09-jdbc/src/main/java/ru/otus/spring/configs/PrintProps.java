package ru.otus.spring.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "string-printer")
public class PrintProps {

    private final Map<String, Map<String, Map<String, String>>> columns = new HashMap<>();

    public Map<String, Map<String, Map<String, String>>> getColumns() {
        return columns;
    }

    public Map<String, Map<String, String>> getColumns(String key) {
        return columns.getOrDefault(key, new HashMap<>());
    }
}
