package ru.otus.spring.repositories;

import ru.otus.spring.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepo {
    List<Author> find();
    Optional<Author> find(long id);
}
