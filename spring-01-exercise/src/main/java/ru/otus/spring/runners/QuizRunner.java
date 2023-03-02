package ru.otus.spring.runners;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.domain.QuizOutlook;

@Component
public class QuizRunner implements CommandLineRunner {

    QuizOutlook quizOutlook;
    AppProps appProps;

    public QuizRunner(QuizOutlook quizOutlook, AppProps appProps) {
        this.quizOutlook = quizOutlook;
        this.appProps = appProps;
    }

    @Override
    public void run(String... args) {
        // Not to start application in test context.
        if (appProps.autorun()) {
            quizOutlook.run();
            System.out.println(quizOutlook.getResultDescription());
        }
    }
}
