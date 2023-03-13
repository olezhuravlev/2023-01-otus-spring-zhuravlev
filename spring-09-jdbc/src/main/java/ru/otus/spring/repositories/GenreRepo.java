package ru.otus.spring.repositories;

import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepo {
    List<Genre> read();
    Optional<Genre> read(long id);
}
