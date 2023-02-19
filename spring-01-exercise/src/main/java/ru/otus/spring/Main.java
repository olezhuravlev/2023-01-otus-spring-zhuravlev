package ru.otus.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.otus.spring.domain.Test;
import ru.otus.spring.domain.TestOutlook;

@ComponentScan(basePackages = "ru.otus")
@Configuration
public class Main {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(Main.class);

        Test test = context.getBean(TestOutlook.class);
        test.run();
        System.out.println(test.getResultDescription());
    }
}
