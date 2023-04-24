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
import ru.otus.shared.model.Author;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Authors repository test")
@DataMongoTest
class AuthorRepoTest {

    @Autowired
    private AuthorRepo authorRepo;

    private static final List<Author> TEST_ITEMS = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        Author item1 = new Author("a1", "Test author 1");
        Author item2 = new Author("a2", "Test author 2");
        Author item3 = new Author("a3", "Test author 3");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3));
    }

    @BeforeEach
    public void beforeEach() {

        authorRepo.deleteAll()
                .then(authorRepo.save(TEST_ITEMS.get(0)))
                .then(authorRepo.save(TEST_ITEMS.get(1)))
                .then(authorRepo.save(TEST_ITEMS.get(2)))
                .block();
    }

    @DisplayName("Retrieve all authors")
    @Test
    void findAll() {

        Flux<Author> publisher = authorRepo.findAll();
        StepVerifier
                .create(publisher)
                .assertNext(author -> assertEquals("a1", author.getId()))
                .assertNext(author -> assertEquals("a2", author.getId()))
                .assertNext(author -> assertEquals("a3", author.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Retrieve author by ID")
    @Test
    void findById() {

        String authorId = "a1";
        String notFoundAuthorId = "a100";

        Mono<Author> publisher = authorRepo.findById(authorId);
        StepVerifier
                .create(publisher)
                .assertNext(author -> assertEquals("a1", author.getId()))
                .expectComplete()
                .verify();

        Mono<Author> notFoundPublisher = authorRepo.findById(notFoundAuthorId);
        StepVerifier
                .create(notFoundPublisher)
                .expectComplete()
                .verify();
    }

    @DisplayName("Delete author by ID")
    @Test
    void delete() {

        String authorId = "a1";
        Flux<Author> publisher = authorRepo
                .findById(authorId)
                .flatMap(item -> authorRepo.delete(item))
                .thenMany(authorRepo.findAll());
        StepVerifier
                .create(publisher)
                .assertNext(author -> assertEquals("a2", author.getId()))
                .assertNext(author -> assertEquals("a3", author.getId()))
                .expectComplete()
                .verify();
    }
}
