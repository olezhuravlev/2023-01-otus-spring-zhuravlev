package ru.otus.restservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.shared.model.Genre;

@Repository
public interface GenreRepo extends ReactiveMongoRepository<Genre, String> {
}
