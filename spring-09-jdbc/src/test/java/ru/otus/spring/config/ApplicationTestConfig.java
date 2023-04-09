package ru.otus.spring.config;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.otus.spring.listener.BookCommentEventListener;
import ru.otus.spring.listener.BookEventListener;

@TestConfiguration
@EnableMongock
public class ApplicationTestConfig {

    @Bean
    public BookEventListener bookEventListener() {
        return new BookEventListener();
    }

    @Bean
    public BookCommentEventListener bookCommentEventListener() {
        return new BookCommentEventListener();
    }
}
