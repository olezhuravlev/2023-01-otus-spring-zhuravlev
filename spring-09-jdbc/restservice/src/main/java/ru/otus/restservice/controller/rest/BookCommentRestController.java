package ru.otus.restservice.controller.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.BookCommentRepo;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.shared.model.BookComment;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BookCommentRestController {

    private final BookCommentRepo bookCommentRepo;

    public BookCommentRestController(BookCommentRepo bookCommentRepo) {
        this.bookCommentRepo = bookCommentRepo;
    }

    @PostMapping(value = "/comments", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BookCommentDto> getAll() {
        return bookCommentRepo.findAll().map(BookCommentDto::toDto);
    }

    @PostMapping(value = "/comments/{id}")
    public Mono<ResponseEntity<BookCommentDto>> getById(@PathVariable("id") String id) {
        return bookCommentRepo.findById(id)
                .map(BookCommentDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/comments")
    public Mono<BookCommentDto> save(@RequestBody BookComment bookComment) {
        return bookCommentRepo.save(bookComment).map(BookCommentDto::toDto);
    }

    @DeleteMapping(value = "/comments/{id}")
    public Mono<ResponseEntity<Map<String, String>>> deleteById(@PathVariable("id") String id) {

        Map<String, String> okResult = new HashMap<>();
        okResult.put("id", id);
        okResult.put("result", "ok");

        return bookCommentRepo.findById(id)
                .flatMap(item -> bookCommentRepo
                        .delete(item)
                        .then(Mono.just(okResult)))
                .map((Map<String, String> t) -> ResponseEntity.accepted().body(t));
    }

    @PostMapping(value = "/comments/book/{bookId}", params = "get")
    public Flux<BookCommentDto> getByBookId(@PathVariable("bookId") String bookId, @RequestParam("get") String get) {
        return bookCommentRepo.findByBookId(bookId)
                .map(BookCommentDto::toDto);
    }

    @PostMapping(value = "/comments/book/{bookId}", params = "exist")
    public Mono<Boolean> existsByBookId(@PathVariable("bookId") String bookId, @RequestParam("exist") String exist) {
        return bookCommentRepo.existsByBookId(bookId);
    }

    @DeleteMapping(value = "/comments/book/{bookId}")
    public Mono<ResponseEntity<Map<String, String>>> deleteByBookId(@PathVariable("bookId") String bookId) {

        Map<String, String> okResult = new HashMap<>();
        okResult.put("bookId", bookId);
        okResult.put("result", "ok");

        return bookCommentRepo.deleteByBookId(bookId)
                .then(Mono.just(okResult))
                .map((Map<String, String> t) -> ResponseEntity.accepted().body(t));
    }
}
