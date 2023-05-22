package ru.otus.spring.testcontainers.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repository.BookCommentRepo;
import ru.otus.spring.repository.BookRepo;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Integration tests for Books")
@AutoConfigureMockMvc
class BookRestControllerIntegrationTest extends AbstractBaseContainer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private BookCommentRepo bookCommentRepo;

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
    }

    @DisplayName("Save Book")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void saveBook() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;

        Optional<Book> bookBefore = bookRepo.findById(initialSequenceId);
        assertThat(bookBefore).isNotPresent();

        Book newBook = new Book(initialSequenceId, "New test book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), new ArrayList<>());
        BookDto source = BookDto.toDto(newBook);

        webTestClient
                .put().uri("/books")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .consumeWith(response -> Assertions.assertThat(response.getResponseBody()).isEqualTo(source));

        // Ensure the book added.
        Optional<Book> bookAfter = bookRepo.findById(initialSequenceId);
        assertThat(bookAfter).isPresent();
    }

    @DisplayName("Save Book by not authenticated user")
    @Test
    @Transactional
    void saveBook_NotAuthenticated() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;

        Book newBook = new Book(initialSequenceId, "New test book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), new ArrayList<>());
        BookDto source = BookDto.toDto(newBook);

        webTestClient
                .put().uri("/books")
                .bodyValue(source)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @DisplayName("Save Book by Anonymous user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void saveBook_Anonymous() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;

        Book newBook = new Book(initialSequenceId, "New test book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), new ArrayList<>());
        BookDto source = BookDto.toDto(newBook);

        webTestClient
                .put().uri("/books")
                .bodyValue(source)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @DisplayName("Save Book by authenticated non-Admin user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_COMMENTER", "ROLE_READER"})
    void saveBook_nonAdmin() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;

        Book newBook = new Book(initialSequenceId, "New test book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), new ArrayList<>());
        BookDto source = BookDto.toDto(newBook);

        webTestClient
                .put().uri("/books")
                .bodyValue(source)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @DisplayName("Delete Book by ID")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteBookById() {

        long bookId = 1;
        long commentId1 = 1;

        // Ensure that book and its comment presented in DB.
        Optional<Book> bookBefore = bookRepo.findById(bookId);
        assertThat(bookBefore).isPresent();

        Optional<BookComment> bookCommentBefore1 = bookCommentRepo.findById(commentId1);
        assertThat(bookCommentBefore1).isPresent();

        webTestClient
                .delete().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, Object> map = response.getResponseBody();
                    Assertions.assertThat(map.getOrDefault("id", null)).isEqualTo((int) bookId);
                    Assertions.assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });

        // Check that the item doesn't exist in DB anymore.
        Optional<Book> bookAfter = bookRepo.findById(bookId);
        assertThat(bookAfter).isNotPresent();

        // All comments of the removed book must be also deleted.
        Optional<BookComment> bookCommentAfter1 = bookCommentRepo.findById(commentId1);
        assertThat(bookCommentAfter1).isNotPresent();
    }

    @DisplayName("Delete Book by ID by not authenticated user")
    @Test
    @Transactional
    void deleteBookById_NotAuthenticated() {

        long bookId = 1;

        webTestClient
                .delete().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @DisplayName("Delete Book by ID by Anonymous user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void deleteBookById_Anonymous() {

        long bookId = 1;

        webTestClient
                .delete().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @DisplayName("Delete Book by ID by non-Admin user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_COMMENTER", "ROLE_READER"})
    void deleteBookById_nonAdmin() {

        long bookId = 1;

        webTestClient
                .delete().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
