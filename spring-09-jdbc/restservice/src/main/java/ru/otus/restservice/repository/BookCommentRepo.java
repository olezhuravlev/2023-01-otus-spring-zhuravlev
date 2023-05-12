package ru.otus.restservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.shared.model.BookComment;

@Repository
public interface BookCommentRepo extends ReactiveMongoRepository<BookComment, String> {
    Mono<Boolean> existsByBookId(String bookId);
    Flux<BookComment> findByBookId(String bookId);
    Mono<Void> deleteByBookId(String bookId);
}
