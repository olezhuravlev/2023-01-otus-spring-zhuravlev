package ru.otus.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.spring.model.Book;

import java.util.List;

public interface BookRepo extends MongoRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String title);
}
