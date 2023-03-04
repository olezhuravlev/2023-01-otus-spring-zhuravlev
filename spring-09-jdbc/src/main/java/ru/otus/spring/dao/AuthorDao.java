package ru.otus.spring.dao;

import ru.otus.spring.model.Author;

import java.util.List;

public interface AuthorDao {
    List<Author> read();
}
