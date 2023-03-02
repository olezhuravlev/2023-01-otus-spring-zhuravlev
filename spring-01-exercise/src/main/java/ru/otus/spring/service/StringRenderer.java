package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.aop.LoggedMethod;

@Component
public class StringRenderer implements Renderer<String> {

    @LoggedMethod
    @Override
    public void render(String value) {
        System.out.println(value);
    }
}
