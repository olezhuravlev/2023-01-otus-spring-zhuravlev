package ru.otus.restservice.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.restservice.repository.BookCommentRepo;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.shared.model.BookComment;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookCommentRestControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookCommentRepo bookCommentRepo;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private static final List<BookComment> TEST_ITEMS = new ArrayList<>();
    private static final List<BookCommentDto> TEST_ITEMS_DTO = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {

        BookComment item1 = new BookComment("bc1", "b1", "Book comment 1");
        BookComment item2 = new BookComment("bc2", "b1", "Book comment 2");
        BookComment item3 = new BookComment("bc3", "b2", "Book comment 3");
        BookComment item4 = new BookComment("bc4", "b2", "Book comment 4");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3, item4));

        TEST_ITEMS_DTO.addAll(TEST_ITEMS.stream().map(BookCommentDto::toDto).toList());
        Collections.reverse(TEST_ITEMS_DTO);
    }

    @BeforeEach
    void beforeEach() {

        bookCommentRepo.deleteAll()
                .then(bookCommentRepo.save(TEST_ITEMS.get(0)))
                .then(bookCommentRepo.save(TEST_ITEMS.get(1)))
                .then(bookCommentRepo.save(TEST_ITEMS.get(2)))
                .then(bookCommentRepo.save(TEST_ITEMS.get(3)))
                .block();
    }

    @DisplayName("Get all book comments")
    @Test
    void getAll() {

        int dataLimit = TEST_ITEMS.size() * 2;

        List<BookCommentDto> result = webTestClient
                .post().uri("/comments")
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookCommentDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).containsExactlyInAnyOrderElementsOf(TEST_ITEMS_DTO);
    }

    @DisplayName("Get book comment by ID")
    @Test
    void getById() {

        String commentId = "bc1";
        String notFoundCommentId = "bc100";

        BookComment source = TEST_ITEMS.stream().filter(comment -> comment.getId().equals(commentId)).findAny().orElse(null);
        BookCommentDto result = BookCommentDto.toDto(source);

        webTestClient
                .post().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookCommentDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));

        webTestClient
                .post().uri(String.format("/comments/%s", notFoundCommentId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Save book comment")
    @Test
    void save() {

        BookComment source = TEST_ITEMS.get(0);
        BookCommentDto result = BookCommentDto.toDto(source);

        webTestClient
                .put().uri("/comments")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookCommentDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));
    }

    @DisplayName("Delete book comment by ID")
    @Test
    void deleteById() {

        String commentId = "bc1";

        int dataLimit = TEST_ITEMS_DTO.size() * 2;
        List<BookCommentDto> expectedResult = TEST_ITEMS_DTO.stream().filter(dto -> !dto.getId().equals(commentId)).toList();

        webTestClient
                .delete().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> map = response.getResponseBody();
                    assertThat(map.getOrDefault("id", null)).isEqualTo(commentId);
                    assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });

        List<BookCommentDto> result = webTestClient
                .post().uri("/comments")
                .contentType(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookCommentDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedResult);
    }

    @DisplayName("Get book comment by book ID")
    @Test
    void getByBookId() {

        String bookId = "b1";

        List<BookComment> result = TEST_ITEMS.stream().filter(comment -> comment.getBookId().equals(bookId)).toList();
        List<BookCommentDto> resultDto = result.stream().map(BookCommentDto::toDto).toList();

        int dataLimit = result.size() * 2;
        List<BookCommentDto> requestResult = webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("/comments/book/%s", bookId))
                        .queryParam("get", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookCommentDto.class)
                .getResponseBody()
                .take(dataLimit)
                .collectList()
                .block();

        assertThat(requestResult).containsExactlyInAnyOrderElementsOf(resultDto);
    }

    @DisplayName("Check if book comments exist for a book identified by ID")
    @Test
    void existsByBookId() {

        String bookId = "b1";
        String noCommentBookId = "b10";

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("/comments/book/%s", bookId))
                        .queryParam("exist", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("/comments/book/%s", noCommentBookId))
                        .queryParam("exist", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @DisplayName("Delete book comments by book ID")
    @Test
    void deleteByBookId() {

        String bookId = "b1";

        webTestClient
                .delete().uri(String.format("/comments/book/%s", bookId))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> map = response.getResponseBody();
                    assertThat(map.getOrDefault("bookId", null)).isEqualTo(bookId);
                    assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });
    }
}
