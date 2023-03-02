package ru.otus.spring.domain;

public interface Quiz {
    void run(String username);
    int getResult();
    String getResultDescription();
}
