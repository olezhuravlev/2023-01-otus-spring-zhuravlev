package ru.otus.spring.controller.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.dto.AuthorDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.service.ApiGate;

import java.util.List;

@RestController
public class AuthorRestController {

    private final ApiGate apiGate;

    public AuthorRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PostMapping("/authors")
    public List<AuthorDto> getAuthors() {
        List<Author> authors = apiGate.getAuthors();
        return authors.stream().map(AuthorDto::toDto).toList();
    }
}
