package ru.otus.spring.service;

import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.dto.BookDto;
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
    Optional<Book> getBook(String id);
    Book saveBook(BookDto dto);
    void deleteBookById(String bookId);

    Optional<BookComment> getBookComment(String commentId);
    BookComment saveBookComment(BookCommentDto dto);
    void deleteBookCommentById(String commentId);
}
