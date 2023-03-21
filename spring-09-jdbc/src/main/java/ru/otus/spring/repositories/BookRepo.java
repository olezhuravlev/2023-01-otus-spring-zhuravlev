package ru.otus.spring.repositories;

import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Optional;

public interface BookRepo {
    List<Book> findAll();
    Optional<Book> find(long bookId);
    List<Book> find(String title);
    Book create(String title, Author author, Genre genre);
    Book save(Book book);
    void delete(Book book);

    List<BookComment> getComments(Book book);
    Optional<BookComment> getComment(Book book, long commentId);
    BookComment createComment(Book book, String text);
    int updateComment(BookComment bookComment, String text);
    int deleteComment(BookComment bookComment);
    int deleteComments(Book book);
}
