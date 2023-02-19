package ru.otus.spring.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.otus.spring.model.Question;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class QuestionParserTest {

    private final static String QUESTIONS_PATH = "questions.csv";

    private static QuestionsParser questionsParser;

    @BeforeClass
    public static void before() {
        questionsParser = new QuestionsParser(new CSVParser());
    }

    @Test
    public void parseTest() {
        List<Question> questions = QuestionParserTest.questionsParser.parse(QUESTIONS_PATH);
        assertEquals("Wrong amount of imported questions", 1, questions.size());
        Question question = questions.get(0);
        assertEquals("Wrong amount of imported answers", 3, question.getAnswers().size());
    }
}
