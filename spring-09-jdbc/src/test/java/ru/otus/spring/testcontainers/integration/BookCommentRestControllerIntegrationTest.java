package ru.otus.spring.testcontainers.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.repository.BookCommentRepo;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Integration tests for Book Comments")
@AutoConfigureMockMvc
class BookCommentRestControllerIntegrationTest extends AbstractBaseContainer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookCommentRepo bookCommentRepo;

    @DisplayName("Save Book Comment")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void saveBookComment() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;
        long bookId = 1;

        Optional<BookComment> bookCommentBefore = bookCommentRepo.findById(initialSequenceId);
        assertThat(bookCommentBefore).isNotPresent();

        BookComment newBookComment = new BookComment(initialSequenceId, "New test book comment", bookId);
        BookCommentDto source = BookCommentDto.toDto(newBookComment);

        webTestClient
                .put().uri("/comments")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookCommentDto.class)
                .consumeWith(response -> Assertions.assertThat(response.getResponseBody()).isEqualTo(source));

        // Ensure book added.
        Optional<BookComment> saved = bookCommentRepo.findById(initialSequenceId);
        assertThat(saved).contains(newBookComment);
    }

    @DisplayName("Save Book Comment by not authenticated user")
    @Test
    @Transactional
    void saveBookComment_NotAuthenticated() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;
        long bookId = 1;

        BookComment newBookComment = new BookComment(initialSequenceId, "New test book comment", bookId);
        BookCommentDto source = BookCommentDto.toDto(newBookComment);

        webTestClient
                .put().uri("/comments")
                .bodyValue(source)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @DisplayName("Save Book Comment by Anonymous user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void saveBookComment_Anonymous() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;
        long bookId = 1;

        BookComment newBookComment = new BookComment(initialSequenceId, "New test book comment", bookId);
        BookCommentDto source = BookCommentDto.toDto(newBookComment);

        webTestClient
                .put().uri("/comments")
                .bodyValue(source)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @DisplayName("Save Book Comment by Commenter user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_COMMENTER"})
    void saveBookComment_Commenter() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;
        long bookId = 1;

        BookComment newBookComment = new BookComment(initialSequenceId, "New test book comment", bookId);
        BookCommentDto source = BookCommentDto.toDto(newBookComment);

        webTestClient
                .put().uri("/comments")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk();
    }

    @DisplayName("Save Book Comment by Reader user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_READER"})
    void saveBookComment_Reader() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;
        long bookId = 1;

        BookComment newBookComment = new BookComment(initialSequenceId, "New test book comment", bookId);
        BookCommentDto source = BookCommentDto.toDto(newBookComment);

        webTestClient
                .put().uri("/comments")
                .bodyValue(source)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @DisplayName("Delete Book Comment by ID")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteBookCommentById() {

        long commentId = 1;

        // Ensure that book and its comment presented in DB.
        Optional<BookComment> bookCommentBefore = bookCommentRepo.findById(commentId);
        assertThat(bookCommentBefore).isPresent();

        webTestClient
                .delete().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, Object> map = response.getResponseBody();
                    Assertions.assertThat(map.getOrDefault("id", null)).isEqualTo((int) commentId);
                    Assertions.assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });

        // Check that the item doesn't exist in DB anymore.
        Optional<BookComment> bookCommentAfter = bookCommentRepo.findById(commentId);
        assertThat(bookCommentAfter).isNotPresent();
    }

    @DisplayName("Delete Book Comment by ID by not authenticated user")
    @Test
    @Transactional
    void deleteBookCommentById_NotAuthenticated() {

        long commentId = 1;

        webTestClient
                .delete().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @DisplayName("Delete Book Comment by ID by Anonymous user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void deleteBookCommentById_Anonymous() {

        long commentId = 1;

        webTestClient
                .delete().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @DisplayName("Delete Book Comment by ID by non-Admin user")
    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_COMMENTER", "ROLE_READER"})
    void deleteBookCommentById_nonAdmin() {

        long commentId = 1;

        webTestClient
                .delete().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
