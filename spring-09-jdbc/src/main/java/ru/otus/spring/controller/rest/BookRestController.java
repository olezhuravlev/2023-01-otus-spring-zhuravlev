package ru.otus.spring.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Book;
import ru.otus.spring.service.ApiGate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BookRestController {

    private final ApiGate apiGate;

    public BookRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookDto saveBook(@Valid @RequestBody BookDto dto) {
        Book savedBook = apiGate.saveBook(dto);
        return BookDto.toDto(savedBook);
    }

    @DeleteMapping(value = "/books/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> deleteBook(@PathVariable("id") String bookId) {

        apiGate.deleteBookById(bookId);

        Map<String, String> result = new HashMap<>();
        result.put("id", bookId);
        result.put("result", "ok");

        return result;
    }
}
