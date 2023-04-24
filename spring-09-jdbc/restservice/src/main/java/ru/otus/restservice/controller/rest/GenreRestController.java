package ru.otus.restservice.controller.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.GenreRepo;
import ru.otus.shared.dto.GenreDto;
import ru.otus.shared.model.Genre;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GenreRestController {

    private final GenreRepo genreRepo;

    public GenreRestController(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    @PostMapping(value = "/genres", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<GenreDto> getAll() {
        return genreRepo.findAll().map(GenreDto::toDto);
    }

    @PostMapping(value = "/genres/{id}")
    public Mono<ResponseEntity<GenreDto>> getById(@PathVariable("id") String id) {
        return genreRepo.findById(id)
                .map(GenreDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/genres")
    public Mono<GenreDto> save(@RequestBody Genre genre) {
        return genreRepo.save(genre).map(GenreDto::toDto);
    }

    @DeleteMapping(value = "/genres/{id}")
    public Mono<ResponseEntity<Map<String, String>>> deleteById(@PathVariable("id") String id) {

        Map<String, String> okResult = new HashMap<>();
        okResult.put("id", id);
        okResult.put("result", "ok");

        return genreRepo.findById(id)
                .flatMap(item -> genreRepo
                        .delete(item)
                        .then(Mono.just(okResult)))
                .map((Map<String, String> t) -> ResponseEntity.accepted().body(t));
    }
}
