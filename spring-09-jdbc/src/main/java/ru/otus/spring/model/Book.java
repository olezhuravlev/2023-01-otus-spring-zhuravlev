package ru.otus.spring.model;

import lombok.Data;

@Data
public class Book {
    private final long id;
    private final String title;
    private final String author;
    private final String genre;
}
