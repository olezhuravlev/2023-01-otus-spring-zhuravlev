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
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.AuthorRepo;
import ru.otus.shared.dto.AuthorDto;
import ru.otus.shared.model.Author;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthorRestControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthorRepo authorRepo;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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

    @BeforeEach
    void beforeEach() {

        authorRepo.deleteAll()
                .then(authorRepo.save(TEST_ITEMS.get(0)))
                .then(authorRepo.save(TEST_ITEMS.get(1)))
                .then(authorRepo.save(TEST_ITEMS.get(2)))
                .block();
    }

    @DisplayName("Get all authors")
    @Test
    void getAll() {

        int dataLimit = TEST_ITEMS.size() * 2;

        List<AuthorDto> result = webTestClient
                .post().uri("/authors")
                .contentType(MediaType.APPLICATION_NDJSON)
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
        AuthorDto author = AuthorDto.toDto(source);

        webTestClient
                .post().uri(String.format("/authors/%s", authorId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(author));

        webTestClient
                .post().uri(String.format("/authors/%s", notFoundAuthorId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Save author")
    @Test
    void save() {

        String authorId = "a100";
        Author newAuthor = new Author(authorId, "Test author 100");
        AuthorDto newAuthorDto = AuthorDto.toDto(newAuthor);

        webTestClient
                .put().uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newAuthor), Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(newAuthorDto));

        webTestClient
                .post().uri(String.format("/authors/%s", authorId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(newAuthorDto));
    }

    @DisplayName("Delete author by ID")
    @Test
    void deleteById() {

        String authorId = "a1";

        int dataLimit = TEST_ITEMS_DTO.size() * 2;
        List<AuthorDto> expectedResult = TEST_ITEMS_DTO.stream().filter(dto -> !dto.getId().equals(authorId)).toList();

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

        List<AuthorDto> result = webTestClient
                .post().uri("/authors")
                .contentType(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedResult);
    }
}
