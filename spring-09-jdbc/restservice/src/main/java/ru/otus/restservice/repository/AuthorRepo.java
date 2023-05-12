package ru.otus.restservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.shared.model.Author;

@Repository
public interface AuthorRepo extends ReactiveMongoRepository<Author, String> {
}
