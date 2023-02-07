package ru.otus.spring.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.otus.spring.TestConfig;
import ru.otus.spring.model.Answer;
import ru.otus.spring.model.Question;
import ru.otus.spring.service.QuestionsParser;
import ru.otus.spring.service.UserProvider;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource("classpath:test.properties")
public class QuestionnaireTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    UserProvider userProvider;

    @Autowired
    QuestionsParser questionsParser;

    @Test
    public void runTest() {

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("Test answer 1"));
        answers.add(new Answer("Test answer 2"));

        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Test question", "1", answers));

        Mockito.when(userProvider.getUserName()).thenReturn("Test user");
        Mockito.when(questionsParser.parse(anyString())).thenReturn(questions);

        // We have to mock tested class if we need to mock methods in it.
        Questionnaire questionnaire = Mockito.spy(context.getBean(Questionnaire.class));

        // Simulate user's input "1" (correct answer).
        Mockito.doReturn("1").when(questionnaire).getUserInput();
        questionnaire.run();

        // One correct answer must be counted in.
        assertEquals("One correct answer must registered", 1, questionnaire.performExam());

        // Simulate user's input "2" (wrong answer).
        Mockito.doReturn("2").when(questionnaire).getUserInput();
        questionnaire.run();

        // No correct answers done..
        assertEquals("No correct answers must registered", 0, questionnaire.performExam());
    }
}
