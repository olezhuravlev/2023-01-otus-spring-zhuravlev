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
    List<Genre> getGenres();

    List<Book> getBooksWithAuthorAndGenre();
    Optional<Book> getBookById(long id);
    Book saveBook(BookDto dto);
    void deleteBookById(long id);

    BookComment saveBookComment(BookCommentDto dto);
    void deleteBookCommentById(long commentId);
}
