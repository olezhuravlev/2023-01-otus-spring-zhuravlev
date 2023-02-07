package ru.otus.spring.service;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import ru.otus.spring.model.Answer;
import ru.otus.spring.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CSVParser implements Parser<Question, CSVRecord> {

    @Override
    public Question parse(CSVRecord record) {

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
        // Record must have not less than 4 rows - a question, a number of correct answer
        // and at least two answers for user to choose.
        if (record == null || record.size() < 4) {
            return false;
        }

        // No one of answers must be empty string.
        return Arrays.stream(record.values()).filter(String::isBlank).findAny().isEmpty();
    }
}
