package ru.otus.spring.service;

public interface Parser<T, S> {
    T parse(S source);
}
