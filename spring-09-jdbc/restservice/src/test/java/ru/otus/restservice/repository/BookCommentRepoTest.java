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
import ru.otus.shared.model.BookComment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Book comments repository test")
@DataMongoTest
class BookCommentRepoTest {

    @Autowired
    private BookCommentRepo bookCommentRepo;

    private static final List<BookComment> TEST_ITEMS = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        BookComment item1 = new BookComment("bc1", "b1", "Test text 1-1");
        BookComment item2 = new BookComment("bc2", "b1", "Test text 1-2");
        BookComment item3 = new BookComment("bc3", "b2", "Test text 2-1");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3));
    }

    @BeforeEach
    public void beforeEach() {

        bookCommentRepo.deleteAll()
                .then(bookCommentRepo.save(TEST_ITEMS.get(0)))
                .then(bookCommentRepo.save(TEST_ITEMS.get(1)))
                .then(bookCommentRepo.save(TEST_ITEMS.get(2)))
                .block();
    }

    @DisplayName("Retrieve all comments")
    @Test
    void findAll() {

        Flux<BookComment> publisher = bookCommentRepo.findAll();
        StepVerifier
                .create(publisher)
                .assertNext(comment -> assertEquals("bc1", comment.getId()))
                .assertNext(comment -> assertEquals("bc2", comment.getId()))
                .assertNext(comment -> assertEquals("bc3", comment.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Retrieve comment by ID")
    @Test
    void findById() {

        String commentId = "bc1";
        String notFoundCommentId = "bc100";

        Mono<BookComment> publisher = bookCommentRepo.findById(commentId);
        StepVerifier
                .create(publisher)
                .assertNext(comment -> assertEquals("bc1", comment.getId()))
                .expectComplete()
                .verify();

        Mono<BookComment> notFoundPublisher = bookCommentRepo.findById(notFoundCommentId);
        StepVerifier
                .create(notFoundPublisher)
                .expectComplete()
                .verify();
    }

    @DisplayName("Delete comment by ID")
    @Test
    void delete() {

        String commentId = "bc1";

        Flux<BookComment> publisher = bookCommentRepo
                .findById(commentId)
                .flatMap(item -> bookCommentRepo.delete(item))
                .thenMany(bookCommentRepo.findAll());
        StepVerifier
                .create(publisher)
                .assertNext(comment -> assertEquals("bc2", comment.getId()))
                .assertNext(comment -> assertEquals("bc3", comment.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Delete comments by book ID")
    @Test
    void deleteByBookId() {

        String bookId = "b1";

        Flux<BookComment> publisher = bookCommentRepo
                .deleteByBookId(bookId)
                .thenMany(bookCommentRepo.findAll());
        StepVerifier
                .create(publisher)
                .assertNext(comment -> assertEquals("bc3", comment.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("Check existence comments for a book by book ID")
    @Test
    void existsByBookId() {

        String bookId = "b1";

        Mono<Boolean> publisher = bookCommentRepo.existsByBookId(bookId);
        StepVerifier.create(publisher)
                .assertNext(aBoolean -> aBoolean.equals(true))
                .expectComplete()
                .verify();

        Mono<Boolean> publisher2 = bookCommentRepo
                .findByBookId(bookId)
                .flatMap(item -> bookCommentRepo.delete(item))
                .then(bookCommentRepo.existsByBookId(bookId));
        StepVerifier.create(publisher2)
                .assertNext(aBoolean -> aBoolean.equals(false))
                .expectComplete()
                .verify();
    }

    @DisplayName("Retrieve comments of a book by book ID")
    @Test
    void findByBookId() {

        String bookId = "b1";

        Flux<BookComment> publisher = bookCommentRepo.findByBookId(bookId);

        StepVerifier.create(publisher)
                .assertNext(comment -> assertEquals("bc1", comment.getId()))
                .assertNext(comment -> assertEquals("bc2", comment.getId()))
                .expectComplete()
                .verify();
    }
}
