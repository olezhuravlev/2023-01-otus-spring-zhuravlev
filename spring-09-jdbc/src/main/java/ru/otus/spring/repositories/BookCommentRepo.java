package ru.otus.spring.repositories;

import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentRepo {
    Optional<BookComment> find(Book book, long commentId);
    BookComment create(Book book, String text);
    BookComment update(Book book, BookComment bookComment, String text);
    Book remove(Book book, BookComment bookComment);
    Book removeAll(Book book);
}
