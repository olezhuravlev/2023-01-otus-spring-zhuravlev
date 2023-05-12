package ru.otus.restservice.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.shared.model.Genre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Genres repository test")
@DataMongoTest
class GenreRepoTest {

    @Autowired
    private GenreRepo genreRepo;

    private static final List<Genre> TEST_ITEMS = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        Genre item1 = new Genre("g1", "Test genre 1");
        Genre item2 = new Genre("g2", "Test genre 2");
        Genre item3 = new Genre("g3", "Test genre 3");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3));
    }

    @BeforeEach
    public void beforeEach() {

        genreRepo.deleteAll()
                .then(genreRepo.save(TEST_ITEMS.get(0)))
                .then(genreRepo.save(TEST_ITEMS.get(1)))
                .then(genreRepo.save(TEST_ITEMS.get(2)))
                .block();
    }

    @DisplayName("Retrieve all genres")
    @Test
    void findAll() {

        Flux<Genre> publisher = genreRepo.findAll();
        StepVerifier
                .create(publisher)
                .assertNext(genre -> assertEquals("g1", genre.getId()))
                .assertNext(genre -> assertEquals("g2", genre.getId()))
                .assertNext(genre -> assertEquals("g3", genre.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Retrieve genre by ID")
    @Test
    void findById() {

        String genreId = "g1";
        String notFoundGenreId = "g100";

        Mono<Genre> publisher = genreRepo.findById(genreId);
        StepVerifier
                .create(publisher)
                .assertNext(genre -> assertEquals("g1", genre.getId()))
                .expectComplete()
                .verify();

        Mono<Genre> notFoundPublisher = genreRepo.findById(notFoundGenreId);
        StepVerifier
                .create(notFoundPublisher)
                .expectComplete()
                .verify();
    }

    @DisplayName("Delete genre by ID")
    @Test
    void delete() {

        String genreId = "g1";
        Flux<Genre> publisher = genreRepo
                .findById(genreId)
                .flatMap(item -> genreRepo.delete(item))
                .thenMany(genreRepo.findAll());
        StepVerifier
                .create(publisher)
                .assertNext(genre -> assertEquals("g2", genre.getId()))
                .assertNext(genre -> assertEquals("g3", genre.getId()))
                .expectComplete()
                .verify();
    }
}
