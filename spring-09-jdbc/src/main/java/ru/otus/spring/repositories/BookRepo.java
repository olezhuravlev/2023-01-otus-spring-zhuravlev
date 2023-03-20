package ru.otus.spring.repositories;

import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookRepo {

    List<Book> findAll();
    Optional<Book> find(long bookId);
    List<Book> find(String title);
    Book save(Book book);
    void delete(Book book);

    List<BookComment> getComments(Book book);
    Optional<BookComment> getComment(Book book, long commentId);
    BookComment createComment(Book book, String text);
    int updateComment(BookComment bookComment, String text);
    int deleteComment(BookComment bookComment);
    int deleteComments(Book book);
}
