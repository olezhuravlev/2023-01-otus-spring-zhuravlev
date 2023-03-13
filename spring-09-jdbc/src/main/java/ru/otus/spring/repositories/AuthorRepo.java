package ru.otus.spring.repositories;

import ru.otus.spring.model.Author;

import java.util.List;

public interface AuthorRepo {
    List<Author> read();
}
