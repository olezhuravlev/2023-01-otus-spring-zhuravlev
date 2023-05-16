package ru.otus.spring.repository;

import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepo {

    List<Book> findAllWithAuthorAndGenre();
    Optional<Book> findById(long id);
    boolean isBookExist(long id);
    Book save(Book book);
    void deleteById(long id);
}
