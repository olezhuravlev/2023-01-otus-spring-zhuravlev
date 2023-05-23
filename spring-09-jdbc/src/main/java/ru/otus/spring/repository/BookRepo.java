package ru.otus.spring.repository;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepo {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<Book> findAllWithAuthorAndGenre();

    Optional<Book> findById(long id);
    boolean isBookExist(long id);
    Book save(Book book);
    void deleteById(long id);
}
