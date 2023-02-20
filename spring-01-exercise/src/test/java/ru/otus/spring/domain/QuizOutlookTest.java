package ru.otus.spring.domain;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.model.Answer;
import ru.otus.spring.model.Question;
import ru.otus.spring.service.QuestionsParser;
import ru.otus.spring.service.Receiver;
import ru.otus.spring.service.Renderer;
import ru.otus.spring.service.UserProvider;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class QuizOutlookTest {

    @MockBean
    UserProvider userProvider;

    @MockBean
    QuestionsParser questionsParser;

    @MockBean
    Receiver receiver;

    @MockBean
    Renderer renderer;

    @Autowired
    @InjectMocks
    QuizOutlook quizOutlook;

    @Test
    public void runTest() {

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("Test answer 1"));
        answers.add(new Answer("Test answer 2"));

        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Test question", "1", answers));

        Mockito.when(userProvider.getUserName("")).thenReturn("Test user");
        Mockito.when(questionsParser.parse(anyString())).thenReturn(questions);

        // Simulate user's input "1" (correct answer).
        Mockito.when(receiver.receive(anyString())).thenReturn("1");
        quizOutlook.run();

        // One correct answer must be given.
        assertEquals(1, quizOutlook.getResult(), "Just one correct answer must be registered");

        // Simulate user's input "2" (wrong answer).
        Mockito.when(receiver.receive(anyString())).thenReturn("2");
        quizOutlook.run();

        // No correct answers must be given.
        assertEquals(0, quizOutlook.getResult(), "No correct answers must be registered");
    }
}
