package ru.otus.spring;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import ru.otus.spring.service.*;

@Import(Main.class)
@Configuration
public class TestConfig {

    @Bean
    @Primary
    public UserProvider userProvider() {
        return Mockito.mock(UserProvider.class);
    }

    @Bean
    @Primary
    public QuestionsParser questionsParser() {
        return Mockito.mock(QuestionsParser.class);
    }

    @Bean
    @Primary
    public Renderer renderer() {
        return Mockito.mock(Renderer.class);
    }

    @Bean
    @Primary
    public Receiver receiver() {
        return Mockito.mock(KeyboardReceiver.class);
    }
}
