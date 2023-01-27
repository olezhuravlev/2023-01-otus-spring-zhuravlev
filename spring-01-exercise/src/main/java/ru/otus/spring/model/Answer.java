package ru.otus.spring.model;

public class Answer {

    private final String text;

    public Answer(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
