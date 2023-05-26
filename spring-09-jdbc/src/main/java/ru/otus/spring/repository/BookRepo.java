package ru.otus.spring.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepo {

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Book> findAllWithAuthorAndGenre();

    @PreAuthorize("@authorizationLogic.decide(#root, #id, 'ru.otus.spring.model.Book', authentication)")
    Optional<Book> findById(@Param("id") long id);

    boolean isBookExist(long id);
    Book save(Book book);
    void deleteById(long id);
}
