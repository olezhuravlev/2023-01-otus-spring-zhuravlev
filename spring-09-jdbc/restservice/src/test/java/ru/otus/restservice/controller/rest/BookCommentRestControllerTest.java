package ru.otus.restservice.controller.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.BookCommentRepo;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.shared.model.BookComment;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(BookCommentRestController.class)
class BookCommentRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookCommentRepo bookCommentRepo;

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

    @DisplayName("Get all book comments")
    @Test
    void getAll() {

        Flux<BookComment> flux = Flux.fromIterable(TEST_ITEMS);
        given(bookCommentRepo.findAll()).willReturn(flux);

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

        Mono<BookComment> mono = Mono.just(source);
        given(bookCommentRepo.findById(commentId)).willReturn(mono);

        webTestClient
                .post().uri(String.format("/comments/%s", commentId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookCommentDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));

        given(bookCommentRepo.findById(notFoundCommentId)).willReturn(Mono.empty());
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

        Mono<BookComment> mono = Mono.just(source);
        given(bookCommentRepo.save(source)).willReturn(mono);

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

        BookComment source = TEST_ITEMS.stream().filter(comment -> comment.getId().equals(commentId)).findAny().orElse(null);

        Mono<BookComment> mono = Mono.just(source);
        given(bookCommentRepo.findById(commentId)).willReturn(mono);
        given(bookCommentRepo.delete(mono.block())).willReturn(Mono.empty());

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
    }

    @DisplayName("Get book comment by book ID")
    @Test
    void getByBookId() {

        String bookId = "b1";

        List<BookComment> result = TEST_ITEMS.stream().filter(comment -> comment.getBookId().equals(bookId)).toList();
        List<BookCommentDto> resultDto = result.stream().map(BookCommentDto::toDto).toList();

        Flux<BookComment> flux = Flux.fromIterable(result);
        given(bookCommentRepo.findByBookId(bookId)).willReturn(flux);

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

        given(bookCommentRepo.existsByBookId(bookId)).willReturn(Mono.just(true));
        given(bookCommentRepo.existsByBookId(noCommentBookId)).willReturn(Mono.just(false));

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

        given(bookCommentRepo.deleteByBookId(bookId)).willReturn(Mono.empty());

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
