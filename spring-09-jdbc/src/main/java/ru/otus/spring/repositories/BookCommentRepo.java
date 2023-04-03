package ru.otus.spring.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.spring.model.BookComment;

public interface BookCommentRepo extends MongoRepository<BookComment, String> {
    void deleteByBookId(String bookId);
}
