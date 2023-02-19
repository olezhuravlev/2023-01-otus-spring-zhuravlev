package ru.otus.spring.service;

import org.springframework.stereotype.Component;

@Component
public class StringRenderer implements Renderer<String> {

    @Override
    public void render(String value) {
        System.out.println(value);
    }
}
