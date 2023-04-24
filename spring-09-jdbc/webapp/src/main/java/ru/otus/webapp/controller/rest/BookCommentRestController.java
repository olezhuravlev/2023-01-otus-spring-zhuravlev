package ru.otus.webapp.controller.rest;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.webapp.service.ApiGate;

import java.util.Map;

@RestController
public class BookCommentRestController {

    private final ApiGate apiGate;

    public BookCommentRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PutMapping(value = "/comments")
    public BookCommentDto saveBookComment(@Valid @RequestBody BookCommentDto dto) {
        return apiGate.saveBookComment(dto);
    }

    @DeleteMapping(value = "/comments/{id}")
    public Map<String, String> deleteBookComment(@PathVariable("id") String commentId) {
        return apiGate.deleteBookCommentById(commentId);
    }
}
