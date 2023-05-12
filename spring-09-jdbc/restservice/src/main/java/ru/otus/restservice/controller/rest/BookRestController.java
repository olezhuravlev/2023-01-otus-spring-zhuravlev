package ru.otus.restservice.controller.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.AuthorRepo;
import ru.otus.restservice.repository.BookCommentRepo;
import ru.otus.restservice.repository.BookRepo;
import ru.otus.restservice.repository.GenreRepo;
import ru.otus.shared.dto.BookDto;
import ru.otus.shared.model.Book;

import java.util.HashMap;
import java.util.Map;


@RestController
public class BookRestController {

    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final BookRepo bookRepo;
    private final BookCommentRepo bookCommentRepo;

    public BookRestController(AuthorRepo authorRepo, GenreRepo genreRepo, BookRepo bookRepo, BookCommentRepo bookCommentRepo) {
        this.authorRepo = authorRepo;
        this.genreRepo = genreRepo;
        this.bookRepo = bookRepo;
        this.bookCommentRepo = bookCommentRepo;
    }

    @PostMapping(value = "/books", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BookDto> getAll() {
        return bookRepo.findAll().flatMap(this::loadRelations).map(BookDto::toDto);
    }

    @PostMapping(value = "/books/{id}")
    public Mono<ResponseEntity<BookDto>> getById(@PathVariable("id") String id) {
        return bookRepo.findById(id)
                .flatMap(this::loadRelations)
                .map(BookDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/books")
    public Mono<BookDto> save(@RequestBody Book book) {
        return bookRepo.save(book)
                .flatMap(this::loadRelations)
                .map(BookDto::toDto);
    }

    @DeleteMapping(value = "/books/{id}")
    public Mono<ResponseEntity<Map<String, String>>> deleteById(@PathVariable("id") String id) {

        Map<String, String> okResult = new HashMap<>();
        okResult.put("id", id);
        okResult.put("result", "ok");

        // If item exists in database, then flatMap(...delete...) will be invoked
        // returning "okResult" map (after deletion book and its comments),
        // and pipeline goes further emitting response status "Accepted" (202) putting the map in the response body.
        // But if item does not exist in database, then pipeline will do nothing immediately returning status "OK" (200)
        // and empty body.
        // Such behavior helps client to distinguish situation whether wanted item was really removed from the database
        // by the request.
        return bookRepo.findById(id)
                .flatMap(item -> bookRepo
                        .delete(item)
                        .then(bookCommentRepo.deleteByBookId(item.getId()))
                        .then(Mono.just(okResult)))
                .map((Map<String, String> t) -> ResponseEntity.accepted().body(t));
    }

    @PostMapping(value = "/books", params = "like")
    public Flux<BookDto> findByTitleContainingIgnoreCase(@RequestParam("like") String name) {
        return bookRepo.findByTitleContainingIgnoreCase(name)
                .flatMap(this::loadRelations).map(BookDto::toDto);
    }

    // SERVICE FUNCTIONS
    private Mono<Book> loadRelations(final Book book) {

        Mono<Book> mono = Mono.just(book)
                .zipWith(authorRepo.findById(book.getAuthorId()))
                .map(tuple -> {
                    tuple.getT1().setAuthorName(tuple.getT2().getName());
                    return tuple.getT1();
                })
                .zipWith(genreRepo.findById(book.getGenreId()))
                .map(tuple -> {
                    tuple.getT1().setGenreName(tuple.getT2().getName());
                    return tuple.getT1();
                });

        return mono;
    }
}
