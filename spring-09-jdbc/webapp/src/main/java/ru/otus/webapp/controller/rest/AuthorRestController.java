package ru.otus.webapp.controller.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.shared.dto.AuthorDto;
import ru.otus.webapp.service.ApiGate;

import java.util.List;

@RestController
public class AuthorRestController {

    private final ApiGate apiGate;

    public AuthorRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PostMapping("/authors")
    public List<AuthorDto> getAuthors() {
        return apiGate.getAuthors();
    }

    @PostMapping("/authors/{id}")
    public AuthorDto getAuthorById(@PathVariable("id") String id) {
        return apiGate.getAuthorById(id);
    }
}
