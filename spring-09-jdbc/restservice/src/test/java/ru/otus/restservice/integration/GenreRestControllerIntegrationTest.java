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
import ru.otus.restservice.repository.GenreRepo;
import ru.otus.shared.dto.GenreDto;
import ru.otus.shared.model.Genre;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class GenreRestControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private GenreRepo genreRepo;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private static final List<Genre> TEST_ITEMS = new ArrayList<>();
    private static final List<GenreDto> TEST_ITEMS_DTO = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {

        Genre item1 = new Genre("g1", "Test genre 1");
        Genre item2 = new Genre("g2", "Test genre 2");
        Genre item3 = new Genre("g3", "Test genre 3");
        TEST_ITEMS.addAll(Arrays.asList(item1, item2, item3));

        TEST_ITEMS_DTO.addAll(TEST_ITEMS.stream().map(GenreDto::toDto).toList());
        Collections.reverse(TEST_ITEMS_DTO);
    }

    @BeforeEach
    void beforeEach() {

        genreRepo.deleteAll()
                .then(genreRepo.save(TEST_ITEMS.get(0)))
                .then(genreRepo.save(TEST_ITEMS.get(1)))
                .then(genreRepo.save(TEST_ITEMS.get(2)))
                .block();
    }

    @DisplayName("Get all genres")
    @Test
    void getAll() {

        int dataLimit = TEST_ITEMS.size() * 2;

        List<GenreDto> result = webTestClient
                .post().uri("/genres")
                .contentType(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).containsExactlyInAnyOrderElementsOf(TEST_ITEMS_DTO);
    }

    @DisplayName("Get genre by ID")
    @Test
    void getById() {

        String genreId = "g1";
        String notFoundGenreId = "g100";

        Genre source = TEST_ITEMS.stream().filter(genre -> genre.getId().equals(genreId)).findAny().orElse(null);
        GenreDto genre = GenreDto.toDto(source);

        webTestClient
                .post().uri(String.format("/genres/%s", genreId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(genre));

        webTestClient
                .post().uri(String.format("/genres/%s", notFoundGenreId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Save genre")
    @Test
    void save() {

        String genreId = "a100";
        Genre newGenre = new Genre(genreId, "Test genre 100");
        GenreDto newGenreDto = GenreDto.toDto(newGenre);

        webTestClient
                .put().uri("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newGenre), Genre.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(newGenreDto));

        webTestClient
                .post().uri(String.format("/genres/%s", genreId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(newGenreDto));
    }

    @DisplayName("Delete genre by ID")
    @Test
    void deleteById() {

        String genreId = "g1";

        int dataLimit = TEST_ITEMS_DTO.size() * 2;
        List<GenreDto> expectedResult = TEST_ITEMS_DTO.stream().filter(dto -> !dto.getId().equals(genreId)).toList();

        webTestClient
                .delete().uri(String.format("/genres/%s", genreId))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> map = response.getResponseBody();
                    assertThat(map.getOrDefault("id", null)).isEqualTo(genreId);
                    assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });

        List<GenreDto> result = webTestClient
                .post().uri("/genres")
                .contentType(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedResult);
    }
}
