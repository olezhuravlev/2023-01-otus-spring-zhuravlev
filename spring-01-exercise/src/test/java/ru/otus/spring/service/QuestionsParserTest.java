package ru.otus.spring.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class QuestionsParserTest {

    @Autowired
    private AppProps appProps;

    private static QuestionsParser questionsParser;

    @BeforeAll
    public static void before() {
        questionsParser = new QuestionsParser(new CSVParser());
    }

    @Test
    public void parseTest() {
        List<Question> questions = QuestionsParserTest.questionsParser.parse(appProps.questionsPath());
        assertEquals(1, questions.size(), "Wrong amount of imported questions");
        Question question = questions.get(0);
        assertEquals(3, question.getAnswers().size(), "Wrong amount of imported answers");
    }
}
