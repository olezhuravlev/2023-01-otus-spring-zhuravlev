package ru.otus.webapp.service;

import ru.otus.shared.dto.AuthorDto;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.shared.dto.BookDto;
import ru.otus.shared.dto.GenreDto;

import java.util.List;
import java.util.Map;

public interface ApiGate {

    List<AuthorDto> getAuthors();

    AuthorDto getAuthorById(String id);

    List<GenreDto> getGenres();

    GenreDto getGenreById(String id);

    List<BookDto> getBooks();

    BookDto getBook(String id);

    BookDto saveBook(BookDto dto);

    Map<String, String> deleteBookById(String id);

    BookCommentDto saveBookComment(BookCommentDto dto);

    Map<String, String> deleteBookCommentById(String id);

    List<BookCommentDto> getCommentsByBookId(String bookId);
}
