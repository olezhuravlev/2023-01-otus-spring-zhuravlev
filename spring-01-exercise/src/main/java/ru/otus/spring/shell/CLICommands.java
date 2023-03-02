package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.domain.QuizOutlook;

@ShellComponent
@RequiredArgsConstructor
public class CLICommands {

    private static final String WELCOME_TEXT = "welcome-text";

    private final QuizOutlook quizOutlook;
    private final MessageSource messageSource;
    private final AppProps appProps;

    private String userName;

    @ShellMethod(value = "login to the Quiz")
    public String login(@ShellOption String userName) {
        this.userName = userName;
        return messageSource.getMessage(WELCOME_TEXT, new String[]{userName}, appProps.locale());
    }

    @ShellMethodAvailability(value = "isUserAuthenticated")
    @ShellMethod(value = "start the Quiz", key = {"start", "run", "begin", "go"})
    public String start() {
        quizOutlook.run(userName);
        return quizOutlook.getResultDescription();
    }

    @ShellMethodAvailability(value = "isUserAuthenticated")
    @ShellMethod(value = "get result of done Quiz", key = {"result", "results", "res"})
    public String getResultDescription() {
        return quizOutlook.getResultDescription();
    }

    private Availability isUserAuthenticated() {
        return userName == null ? Availability.unavailable("you are not logged in") : Availability.available();
    }
}
