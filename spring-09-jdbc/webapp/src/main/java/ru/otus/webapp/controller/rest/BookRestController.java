package ru.otus.webapp.controller.rest;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.otus.shared.dto.BookDto;
import ru.otus.webapp.service.ApiGate;

import java.util.Map;

@RestController
public class BookRestController {

    private final ApiGate apiGate;

    public BookRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PutMapping(value = "/books")
    public BookDto saveBook(@Valid @RequestBody BookDto dto) {
        return apiGate.saveBook(dto);
    }

    @DeleteMapping(value = "/books/{id}")
    public Map<String, String> deleteBook(@PathVariable("id") String bookId) {
        return apiGate.deleteBookById(bookId);
    }
}
