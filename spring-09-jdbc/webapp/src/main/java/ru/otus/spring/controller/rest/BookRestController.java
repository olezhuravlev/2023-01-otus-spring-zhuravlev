package ru.otus.spring.controller.rest;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BookRestController {

    private final ApiGate apiGate;

    public BookRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PutMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookDto saveBook(@Valid @RequestBody BookDto dto) {

        Book savedBook = apiGate.saveBook(dto);

        savedBook.setAuthor(Hibernate.unproxy(savedBook.getAuthor(), Author.class));
        savedBook.setGenre(Hibernate.unproxy(savedBook.getGenre(), Genre.class));

        return BookDto.toDto(savedBook);
    }

    @DeleteMapping(value = "/books/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteBookById(@PathVariable("id") long bookId) {

        apiGate.deleteBookById(bookId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", bookId);
        result.put("result", "ok");

        return result;
    }
}
