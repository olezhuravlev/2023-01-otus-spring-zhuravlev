package ru.otus.spring.domain;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Question;
import ru.otus.spring.service.Parser;
import ru.otus.spring.service.Receiver;
import ru.otus.spring.service.Renderer;
import ru.otus.spring.service.UserProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:application.yaml")
public class QuizOutlook implements Quiz {

    private static final String ENTER_NAME_KEY = "enter-name";
    private static final String ANSWER_NUMBER_KEY = "answer-num";
    private static final String CORRECT_KEY = "correct";
    private static final String WRONG_KEY = "wrong";
    private static final String NOT_DONE_MESSAGE_KEY = "is-not-done";
    private static final String SUCCESS_MESSAGE_KEY = "success";
    private static final String FAIL_MESSAGE_KEY = "fail";

    private final AppProps appProps;
    private final MessageSource messageSource;

    private final List<Question> questionList;
    private String userName;

    private final UserProvider userProvider;
    private final Parser questionsParser;
    private final Receiver receiver;
    private final Renderer renderer;

    private int result;

    public QuizOutlook(UserProvider userProvider, Parser questionsParser, Receiver receiver, Renderer renderer, AppProps appProps, MessageSource messageSource) {
        this.userProvider = userProvider;
        this.questionsParser = questionsParser;
        this.receiver = receiver;
        this.renderer = renderer;
        this.questionList = new ArrayList<>();
        this.userName = "";
        this.result = -1;
        this.appProps = appProps;
        this.messageSource = messageSource;
    }

    @Override
    public void run() {
        List<Question> questions = (List<Question>) questionsParser.parse(appProps.questionsPath());
        questionList.addAll(questions);
        result = performExam();
    }

    @Override
    public int getResult() {
        return result;
    }

    @Override
    public String getResultDescription() {

        var locale = appProps.locale();

        if (result < 0) {
            return messageSource.getMessage(NOT_DONE_MESSAGE_KEY, null, locale);
        }

        int questions = questionList.size();
        double correctAnswersShare = 0;
        if (questions > 0) {
            correctAnswersShare = (double) result / questions * 100;
        }

        String resultMessage;
        if (correctAnswersShare > appProps.successThreshold()) {
            resultMessage = messageSource.getMessage(SUCCESS_MESSAGE_KEY, null, locale);
        } else {
            resultMessage = messageSource.getMessage(FAIL_MESSAGE_KEY, null, locale);
        }

        return String.format(resultMessage, userName, (int) correctAnswersShare + "%", appProps.successThreshold() + "%");
    }

    private int performExam() {

        var locale = appProps.locale();

        int correctAnswersCounter = 0;
        String welcomeText = messageSource.getMessage(ENTER_NAME_KEY, null, locale);
        userName = userProvider.getUserName(welcomeText);

        for (Question question : questionList) {

            renderer.render(question.toString());

            String answerNumberText = messageSource.getMessage(ANSWER_NUMBER_KEY, null, locale);
            String userAnswer = receiver.receive(answerNumberText);

            String message;
            if (question.getCorrectAnswerPosition().equals(userAnswer)) {
                ++correctAnswersCounter;
                message = messageSource.getMessage(CORRECT_KEY, null, locale);
            } else {
                message = messageSource.getMessage(WRONG_KEY, null, locale);
            }
            renderer.render(message);
        }

        return correctAnswersCounter;
    }
}
