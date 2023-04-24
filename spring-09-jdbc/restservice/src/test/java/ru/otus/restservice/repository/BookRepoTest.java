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
import ru.otus.shared.model.Book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Books repository test")
@DataMongoTest
class BookRepoTest {

    @Autowired
    private BookRepo bookRepo;

    private static final List<Book> TEST_ITEMS = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        Book item1 = new Book("b1", "Test book 1", "a1", "", "g1", "");
        Book item2 = new Book("b2", "Test book 2", "a1", "", "g2", "");
        Book item3 = new Book("b3", "Test book 3", "a2", "", "g2", "");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3));
    }

    @BeforeEach
    public void beforeEach() {

        bookRepo.deleteAll()
                .then(bookRepo.save(TEST_ITEMS.get(0)))
                .then(bookRepo.save(TEST_ITEMS.get(1)))
                .then(bookRepo.save(TEST_ITEMS.get(2)))
                .block();
    }

    @DisplayName("Retrieve all books")
    @Test
    void findAll() {

        Flux<Book> publisher = bookRepo.findAll();
        StepVerifier
                .create(publisher)
                .assertNext(book -> assertEquals("b1", book.getId()))
                .assertNext(book -> assertEquals("b2", book.getId()))
                .assertNext(book -> assertEquals("b3", book.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Retrieve all books")
    @Test
    void findByTitleContainingIgnoreCase() {

        String bookTitle = "BOOK";

        Flux<Book> publisher = bookRepo.findByTitleContainingIgnoreCase(bookTitle);
        StepVerifier
                .create(publisher)
                .assertNext(book -> assertEquals("Test book 1", book.getTitle()))
                .assertNext(book -> assertEquals("Test book 2", book.getTitle()))
                .assertNext(book -> assertEquals("Test book 3", book.getTitle()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Retrieve book by ID")
    @Test
    void findById() {

        String bookId = "b1";
        String notFoundBookId = "b100";

        Mono<Book> publisher = bookRepo.findById(bookId);
        StepVerifier
                .create(publisher)
                .assertNext(book -> assertEquals("b1", book.getId()))
                .expectComplete()
                .verify();

        Mono<Book> notFoundPublisher = bookRepo.findById(notFoundBookId);
        StepVerifier
                .create(notFoundPublisher)
                .expectComplete()
                .verify();
    }

    @DisplayName("Delete book by ID")
    @Test
    void delete() {

        String bookId = "b1";
        Flux<Book> publisher = bookRepo
                .findById(bookId)
                .flatMap(item -> bookRepo.delete(item))
                .thenMany(bookRepo.findAll());
        StepVerifier
                .create(publisher)
                .assertNext(book -> assertEquals("b2", book.getId()))
                .assertNext(book -> assertEquals("b3", book.getId()))
                .expectComplete()
                .verify();
    }
}
