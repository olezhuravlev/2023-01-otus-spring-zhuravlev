package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.model.Book;

public interface BookRepo extends JpaRepository<Book, Long> {
}
