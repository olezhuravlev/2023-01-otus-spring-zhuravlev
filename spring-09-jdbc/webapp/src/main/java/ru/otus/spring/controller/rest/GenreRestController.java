package ru.otus.spring.controller.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.dto.GenreDto;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;

import java.util.List;

@RestController
public class GenreRestController {

    private final ApiGate apiGate;

    public GenreRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PostMapping("/genres")
    public List<GenreDto> getGenres() {
        List<Genre> genres = apiGate.getGenres();
        return genres.stream().map(GenreDto::toDto).toList();
    }
}
