package ru.otus.spring.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.otus.spring.model.Question;
import ru.otus.spring.service.QuestionsParser;
import ru.otus.spring.service.UserProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@PropertySource("classpath:questionnaire.properties")
public class Questionnaire {

    private final List<Question> questionList;
    private String userName;

    private final UserProvider userProvider;
    private final QuestionsParser questionsParser;

    @Value("${successMessage}")
    private String successMessage;

    @Value("${failMessage}")
    private String failMessage;

    @Value("${questionsPath}")
    private String questionsPath;

    @Value("${successThreshold}")
    private int successThreshold;

    public Questionnaire(UserProvider userProvider, QuestionsParser questionsParser) {
        this.userProvider = userProvider;
        this.questionsParser = questionsParser;
        this.questionList = new ArrayList<>();
        this.userName = "";
    }

    public void run() {

        List<Question> questions = questionsParser.parse(questionsPath);
        questionList.addAll(questions);

        int correctAnswers = performExam();
        printResult(correctAnswers);
    }

    public int performExam() {

        int correctAnswersCounter = 0;
        userName = userProvider.getUserName();

        for (Question question : questionList) {
            String text = question.toString();
            System.out.println(text);
            System.out.println("Enter number of your answer:");

            String userAnswer = getUserInput();
            if (question.getCorrectAnswerPosition().equals(userAnswer)) {
                ++correctAnswersCounter;
                System.out.println("Correct!\n");
            } else {
                System.out.println("Wrong!\n");
            }
        }

        return correctAnswersCounter;
    }

    public String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void printResult(int correctAnswersCounter) {

        double correctAnswersShare = (double) correctAnswersCounter / questionList.size() * 100;

        String resultMessage;
        if (correctAnswersShare > successThreshold) {
            resultMessage = successMessage;
        } else {
            resultMessage = failMessage;
        }

        System.out.printf(resultMessage, userName, (int) correctAnswersShare + "%", successThreshold + "%");
    }
}
