package ru.otus.spring.service;

public interface Renderer<T> {
    void render(T value);
}
