package ru.otus.spring.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.repository.BookCommentRepo;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Integration tests for Book Comments")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BookCommentRestControllerIntegrationTest {

    private static final String DATABASE_NAME = "librarydb_test";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookCommentRepo bookCommentRepo;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @DisplayName("Save Book Comment")
    @Test
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

    @DisplayName("Delete Book Comment by ID")
    @Test
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
}
