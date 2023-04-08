package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.model.BookComment;

public interface BookCommentRepo extends JpaRepository<BookComment, Long> {
    int deleteByBookId(long bookId);
}
