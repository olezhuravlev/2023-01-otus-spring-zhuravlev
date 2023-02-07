package ru.otus.spring.domain;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import ru.otus.spring.model.Answer;
import ru.otus.spring.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Questionnaire implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final List<Question> questionList;
    private final Scanner scanner;
    private final String questionsPath;
    private final int successThreshold;
    private String userName;

    private final static String SUCCESS_MESSAGE = "Well done %s! You've answered %s of questions (%s required) and successfully passed the exam!";
    private final static String FAIL_MESSAGE = "Sorry %s, you've answered %s of questions (%s required) and failed the exam!";

    public Questionnaire(String questionsPath, int successThreshold) {
        this.questionsPath = questionsPath;
        this.successThreshold = successThreshold;
        this.questionList = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.userName = "";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void run() {
        questionList.addAll(parseContent());
        int correctAnswers = performExam();
        printResult(correctAnswers);
    }

    private List<Question> parseContent() {

        List<Question> result = new ArrayList<>();

        Resource resource = applicationContext.getResource(questionsPath);
        try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream())) {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(bufferedReader);
            for (CSVRecord record : records) {
                Question question = parseQuestion(record);
                if (question != null) {
                    result.add(question);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Question parseQuestion(CSVRecord record) {

        if (!isCorrect(record)) {
            return null;
        }

        String[] values = record.values();
        String questionText = values[0];
        String correctAnswerPosition = values[1];

        List<Answer> answers = new ArrayList<>();
        for (var i = 2; i < record.size(); i++) {
            Answer answer = new Answer(values[i]);
            answers.add(answer);
        }

        return new Question(questionText, correctAnswerPosition, answers);
    }

    private boolean isCorrect(CSVRecord record) {

        if (record == null || record.size() < 4) {
            return false;
        }

        return Arrays.stream(record.values()).filter(String::isBlank).findAny().isEmpty();
    }

    private void obtainUserName() {
        while (userName.isBlank()) {
            System.out.println("Enter your name:");
            userName = scanner.nextLine();
        }
    }

    private int performExam() {

        int correctAnswersCounter = 0;

        obtainUserName();

        for (Question question : questionList) {
            String text = question.toString();
            System.out.println(text);
            System.out.println("Enter number of your answer:");

            String userAnswer = scanner.nextLine();
            if (question.getCorrectAnswerPosition().equals(userAnswer)) {
                ++correctAnswersCounter;
                System.out.println("Correct!\n");
            } else {
                System.out.println("Wrong!\n");
            }
        }

        return correctAnswersCounter;
    }

    private void printResult(int correctAnswersCounter) {
        double correctAnswersShare = (double) correctAnswersCounter / questionList.size() * 100;
        String resultMessage;
        if (correctAnswersShare > successThreshold) {
            resultMessage = SUCCESS_MESSAGE;
        } else {
            resultMessage = FAIL_MESSAGE;
        }
        System.out.printf(resultMessage, userName, (int) correctAnswersShare + "%", successThreshold + "%");
    }
}
