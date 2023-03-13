package ru.otus.spring.repositories;

import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;

import java.util.List;

public interface BookCommentRepo {
    BookComment read(long id);
    List<BookComment> readAll(long id_book);
    int create(long id_book, String text);
    int update(long id_book, String text);
    int delete(long id);
}
