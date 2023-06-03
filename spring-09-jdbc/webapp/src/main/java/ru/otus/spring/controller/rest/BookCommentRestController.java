package ru.otus.spring.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.service.ApiGate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BookCommentRestController {

    private final ApiGate apiGate;

    public BookCommentRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PutMapping(value = "/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookCommentDto saveBookComment(@Valid @RequestBody BookCommentDto dto) {
        BookComment savedBookComment = apiGate.saveBookComment(dto);
        return BookCommentDto.toDto(savedBookComment);
    }

    @DeleteMapping(value = "/comments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteBookCommentById(@PathVariable("id") long commentId) {

        apiGate.deleteBookCommentById(commentId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", commentId);
        result.put("result", "ok");

        return result;
    }
}
