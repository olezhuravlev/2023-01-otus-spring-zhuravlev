package ru.otus.spring.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import ru.otus.spring.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionsParser implements Parser<List<Question>, String> {

    private final Parser csvParser;

    public QuestionsParser(Parser csvParser) {
        this.csvParser = csvParser;
    }

    @Override
    public List<Question> parse(String path) {

        List<Question> result = new ArrayList<>();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            return result;
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(bufferedReader);
            for (CSVRecord record : records) {
                Question question = (Question) csvParser.parse(record);
                if (question != null) {
                    result.add(question);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
