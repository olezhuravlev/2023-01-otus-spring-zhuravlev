package ru.otus.restservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.shared.model.Book;

@Repository
public interface BookRepo extends ReactiveMongoRepository<Book, String> {
    Flux<Book> findByTitleContainingIgnoreCase(String title);
}
