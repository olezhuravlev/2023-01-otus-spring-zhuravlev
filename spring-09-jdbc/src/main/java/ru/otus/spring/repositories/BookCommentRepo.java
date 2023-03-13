package ru.otus.spring.repositories;

import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentRepo {
    Optional<BookComment> read(Book book, long comment_id);
    List<BookComment> read(Book book);
    BookComment create(Book book, String text);
    BookComment update(Book book, BookComment bookComment, String text);
    Book delete(Book book, BookComment bookComment);
    Book deleteAll(Book book);
}
