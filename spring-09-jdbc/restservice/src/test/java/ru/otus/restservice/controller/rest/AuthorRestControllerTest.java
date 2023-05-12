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
import ru.otus.restservice.repository.AuthorRepo;
import ru.otus.shared.dto.AuthorDto;
import ru.otus.shared.model.Author;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorRepo authorRepo;

    private static final List<Author> TEST_ITEMS = new ArrayList<>();
    private static final List<AuthorDto> TEST_ITEMS_DTO = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {

        Author item1 = new Author("a1", "Test author 1");
        Author item2 = new Author("a2", "Test author 2");
        Author item3 = new Author("a3", "Test author 3");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3));

        TEST_ITEMS_DTO.addAll(TEST_ITEMS.stream().map(AuthorDto::toDto).toList());
        Collections.reverse(TEST_ITEMS_DTO);
    }

    @DisplayName("Get all authors")
    @Test
    void getAll() {

        Flux<Author> flux = Flux.fromIterable(TEST_ITEMS);
        given(authorRepo.findAll()).willReturn(flux);

        int dataLimit = TEST_ITEMS.size() * 2;
        List<AuthorDto> result = webTestClient
                .post().uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).containsExactlyInAnyOrderElementsOf(TEST_ITEMS_DTO);
    }

    @DisplayName("Get author by ID")
    @Test
    void getById() {

        String authorId = "a1";
        String notFoundAuthorId = "a100";

        Author source = TEST_ITEMS.stream().filter(author -> author.getId().equals(authorId)).findAny().orElse(null);
        AuthorDto result = AuthorDto.toDto(source);

        Mono<Author> mono = Mono.just(source);
        given(authorRepo.findById(authorId)).willReturn(mono);

        webTestClient
                .post().uri(String.format("/authors/%s", authorId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));

        given(authorRepo.findById(notFoundAuthorId)).willReturn(Mono.empty());
        webTestClient
                .post().uri(String.format("/authors/%s", notFoundAuthorId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Save author")
    @Test
    void save() {

        Author source = TEST_ITEMS.get(0);
        AuthorDto result = AuthorDto.toDto(source);

        Mono<Author> mono = Mono.just(source);
        given(authorRepo.save(source)).willReturn(mono);

        webTestClient
                .put().uri("/authors")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));
    }

    @DisplayName("Delete author by ID")
    @Test
    void deleteById() {

        String authorId = "a1";

        Author source = TEST_ITEMS.stream().filter(author -> author.getId().equals(authorId)).findAny().orElse(null);

        Mono<Author> mono = Mono.just(source);
        given(authorRepo.findById(authorId)).willReturn(mono);
        given(authorRepo.delete(mono.block())).willReturn(Mono.empty());

        webTestClient
                .delete().uri(String.format("/authors/%s", authorId))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> map = response.getResponseBody();
                    assertThat(map.getOrDefault("id", null)).isEqualTo(authorId);
                    assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });
    }
}
