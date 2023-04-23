package ru.otus.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.spring.model.BookComment;

public interface BookCommentRepo extends MongoRepository<BookComment, String> {
    int deleteByBookId(String bookId);
}
