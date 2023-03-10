package ru.otus.spring.configs;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppProps.class, PrintProps.class})
public class ApplicationConfig {
}
