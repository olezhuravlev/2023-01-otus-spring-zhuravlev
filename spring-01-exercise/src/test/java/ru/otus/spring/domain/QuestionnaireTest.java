package ru.otus.spring.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.otus.spring.model.Question;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
public class QuestionnaireTest implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Test
    public void questionnaireTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Questionnaire questionnaire = context.getBean(Questionnaire.class);

        List<Question> questions = (List<Question>) getParseContentMethod().invoke(questionnaire);

        assertEquals("Wrong amount of imported questions", 1, questions.size());
        Question question = questions.get(0);
        assertEquals("Wron amount of imported answers", 3, question.getAnswers().size());
    }

    private Method getParseContentMethod() throws NoSuchMethodException {
        Method method = Questionnaire.class.getDeclaredMethod("parseContent");
        method.setAccessible(true);
        return method;
    }
}
