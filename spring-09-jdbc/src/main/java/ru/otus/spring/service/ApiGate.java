package ru.otus.spring.service;

import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Optional;

public interface ApiGate {

    List<Author> getAuthors();
    Optional<Author> getAuthor(String id);

    List<Genre> getGenres();
    Optional<Genre> getGenre(String id);

    List<Book> getBooks();
    Optional<Book> getBookById(String id);
    boolean isBookExist(String id);
    List<Book> findBooksByTitle(String title);
    Book save(String title, String authorId, String genreId);
    Book update(Book book);
    void deleteBook(Book book);

    List<BookComment> getBookComments(Book book);
    Optional<BookComment> getBookComment(String commentId);
    boolean isBookCommentExist(String id);
    BookComment createBookComment(String bookId, String text);
    BookComment updateBookComment(String bookCommentId, String text);
    void deleteBookCommentById(String commentId);
    void deleteCommentsByBookId(String bookId);
}
