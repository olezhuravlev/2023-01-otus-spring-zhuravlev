package ru.otus.spring.repository;

import ru.otus.spring.model.BookComment;

import java.util.Optional;

public interface BookCommentRepo {

    Optional<BookComment> findById(long commentId);
    boolean isBookCommentExist(long commentId);
    BookComment save(BookComment bookComment);
    void deleteCommentById(long commentId);
}
