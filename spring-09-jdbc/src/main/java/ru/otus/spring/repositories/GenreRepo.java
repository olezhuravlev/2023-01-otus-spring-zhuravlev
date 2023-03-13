package ru.otus.spring.repositories;

import ru.otus.spring.model.Genre;

import java.util.List;

public interface GenreRepo {
    List<Genre> read();
}
