package ru.otus.spring.repositories;

import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepo {
    List<Book> findAllWithAuthorAndGenre();
    Optional<Book> findById(long id);
    Optional<Book> findByIdWithAuthorAndGenre(long id);
    List<Book> findByTitle(String title);
    boolean isBookExist(long id);
    Book save(Book book);
    void delete(Book book);
}
