package ru.otus.spring.service;

import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Optional;

public interface ApiGate {
    List<Author> getAuthors();
    Optional<Author> getAuthor(long id);
    List<Genre> getGenres();
    Optional<Genre> getGenre(long id);
    List<Book> getBooksWithAuthorAndGenre();
    Optional<Book> getBookById(long id);
    Optional<Book> getBookByIdWithAuthorAndGenre(long id);
    boolean isBookExist(long id);
    List<Book> findBooksByTitle(String title);
    Book save(String title, long authorId, long genreId);
    Book update(Book book);
    void deleteBook(Book book);
    List<BookComment> getCommentsByBook(Book book);
    Optional<BookComment> getBookComment(long commentID);
    boolean isBookCommentExist(long id);
    BookComment createBookComment(long bookId, String text);
    BookComment updateBookComment(long bookCommentId, String text);
    void deleteBookCommentById(long commentId);
    int deleteCommentsByBookId(long bookId);
}
