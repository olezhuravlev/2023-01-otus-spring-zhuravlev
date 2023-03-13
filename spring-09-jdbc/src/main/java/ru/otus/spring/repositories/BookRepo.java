package ru.otus.spring.repositories;

import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Optional;

public interface BookRepo {
    List<Book> read();
    Optional<Book> read(long id);
    List<Book> read(String title);
    Book create(String title, Author author, Genre genre);
    int update(long id, String title, Author author, Genre genre);
    int delete(long id);
}
