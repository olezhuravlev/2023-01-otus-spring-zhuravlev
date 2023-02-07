package ru.otus.spring.model;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private final String text;
    private final String correctAnswerPosition;
    private final List<Answer> answers;

    public Question(String text, String correctAnswerPosition, List<Answer> answers) {
        this.text = text;
        this.correctAnswerPosition = correctAnswerPosition;
        this.answers = new ArrayList<>(answers);
    }


    public List<Answer> getAnswers() {
        return new ArrayList<>(answers);
    }

    public String getCorrectAnswerPosition() {
        return correctAnswerPosition;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        for (var i = 0; i < answers.size(); i++) {
            String answer = answers.get(i).toString();
            builder.append("[" + (i + 1) + "] " + answer + "\n");
        }

        return text + "\n" + builder.toString();
    }
}
