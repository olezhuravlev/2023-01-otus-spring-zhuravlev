package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepoEager extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findAll();

    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findByTitleContainingIgnoreCase(String title);

    @EntityGraph(attributePaths = "bookComments")
    Optional<Book> findWithCommentsById(Long id);
}
