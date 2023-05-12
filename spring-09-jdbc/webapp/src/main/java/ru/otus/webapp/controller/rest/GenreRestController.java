package ru.otus.webapp.controller.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.shared.dto.GenreDto;
import ru.otus.webapp.service.ApiGate;

import java.util.List;

@RestController
public class GenreRestController {

    private final ApiGate apiGate;

    public GenreRestController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @PostMapping("/genres")
    public List<GenreDto> getGenres() {
        return apiGate.getGenres();
    }

    @PostMapping("/genres/{id}")
    public GenreDto getGenreById(@PathVariable("id") String id) {
        return apiGate.getGenreById(id);
    }
}
