package ru.otus.spring.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.otus.spring.model.Question;
import ru.otus.spring.service.QuestionsParser;
import ru.otus.spring.service.Receiver;
import ru.otus.spring.service.Renderer;
import ru.otus.spring.service.UserProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:test_outlook.properties")
public class TestOutlook implements Test {

    private final List<Question> questionList;
    private String userName;

    private final UserProvider userProvider;
    private final QuestionsParser questionsParser;
    private final Receiver receiver;
    private final Renderer renderer;

    private int result;

    @Value("${successMessage}")
    private String successMessage;

    @Value("${failMessage}")
    private String failMessage;

    @Value("${isNotDoneMessage}")
    private String isNotDoneMessage;

    @Value("${questionsPath}")
    private String questionsPath;

    @Value("${successThreshold}")
    private int successThreshold;

    public TestOutlook(UserProvider userProvider, QuestionsParser questionsParser, Receiver receiver, Renderer renderer) {
        this.userProvider = userProvider;
        this.questionsParser = questionsParser;
        this.receiver = receiver;
        this.renderer = renderer;
        this.questionList = new ArrayList<>();
        this.userName = "";
        this.result = -1;
    }

    @Override
    public void run() {
        List<Question> questions = questionsParser.parse(questionsPath);
        questionList.addAll(questions);
        result = performExam();
    }

    private int performExam() {

        int correctAnswersCounter = 0;
        userName = userProvider.getUserName();

        for (Question question : questionList) {

            renderer.render(question.toString());

            String userAnswer = receiver.receive("Enter number of your answer:");

            String message;
            if (question.getCorrectAnswerPosition().equals(userAnswer)) {
                ++correctAnswersCounter;
                message = "Correct!\n";
            } else {
                message = "Wrong!\n";
            }
            renderer.render(message);
        }

        return correctAnswersCounter;
    }

    @Override
    public int getResult() {
        return result;
    }

    @Override
    public String getResultDescription() {

        if (result < 0) {
            return isNotDoneMessage;
        }

        double correctAnswersShare = (double) result / questionList.size() * 100;

        String resultMessage;
        if (correctAnswersShare > successThreshold) {
            resultMessage = successMessage;
        } else {
            resultMessage = failMessage;
        }

        return String.format(resultMessage, userName, (int) correctAnswersShare + "%", successThreshold + "%");
    }
}
