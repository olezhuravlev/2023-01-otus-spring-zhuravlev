package ru.otus.restservice.controller.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.AuthorRepo;
import ru.otus.shared.dto.AuthorDto;
import ru.otus.shared.model.Author;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthorRestController {

    private final AuthorRepo authorRepo;

    public AuthorRestController(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    @PostMapping(value = "/authors", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AuthorDto> getAll() {
        return authorRepo.findAll().map(AuthorDto::toDto);
    }

    @PostMapping(value = "/authors/{id}")
    public Mono<ResponseEntity<AuthorDto>> getById(@PathVariable("id") String id) {
        return authorRepo.findById(id)
                .map(AuthorDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/authors")
    public Mono<AuthorDto> save(@RequestBody Author author) {
        return authorRepo.save(author).map(AuthorDto::toDto);
    }

    @DeleteMapping(value = "/authors/{id}")
    public Mono<ResponseEntity<Map<String, String>>> deleteById(@PathVariable("id") String id) {

        Map<String, String> okResult = new HashMap<>();
        okResult.put("id", id);
        okResult.put("result", "ok");

        return authorRepo.findById(id)
                .flatMap(item -> authorRepo
                        .delete(item)
                        .then(Mono.just(okResult)))
                .map((Map<String, String> t) -> ResponseEntity.accepted().body(t));
    }
}
