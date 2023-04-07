package ru.otus.spring.configs;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ru.otus.spring.listeners.BookEventListener;

import java.text.MessageFormat;
import java.util.Locale;

@TestConfiguration
@EnableMongock
@EnableMongoRepositories(basePackages = "ru.otus.spring.repositories")
@EnableConfigurationProperties({AppProps.class, PrintProps.class})
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

    @Bean
    public BookEventListener bookEventListener() {
        return new BookEventListener();
    }
}
