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
import ru.otus.restservice.repository.GenreRepo;
import ru.otus.shared.dto.GenreDto;
import ru.otus.shared.model.Genre;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreRepo genreRepo;

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

    @DisplayName("Get all genres")
    @Test
    void getAll() {

        Flux<Genre> flux = Flux.fromIterable(TEST_ITEMS);
        given(genreRepo.findAll()).willReturn(flux);

        int dataLimit = TEST_ITEMS.size() * 2;
        List<GenreDto> result = webTestClient
                .post().uri("/genres")
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
        GenreDto result = GenreDto.toDto(source);

        Mono<Genre> mono = Mono.just(source);
        given(genreRepo.findById(genreId)).willReturn(mono);

        webTestClient
                .post().uri(String.format("/genres/%s", genreId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));

        given(genreRepo.findById(notFoundGenreId)).willReturn(Mono.empty());
        webTestClient
                .post().uri(String.format("/genres/%s", notFoundGenreId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Save genre")
    @Test
    void save() {

        Genre source = TEST_ITEMS.get(0);
        GenreDto result = GenreDto.toDto(source);

        Mono<Genre> mono = Mono.just(source);
        given(genreRepo.save(source)).willReturn(mono);

        webTestClient
                .put().uri("/genres")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));
    }

    @DisplayName("Delete genre by ID")
    @Test
    void deleteById() {

        String genreId = "g1";

        Genre source = TEST_ITEMS.stream().filter(genre -> genre.getId().equals(genreId)).findAny().orElse(null);

        Mono<Genre> mono = Mono.just(source);
        given(genreRepo.findById(genreId)).willReturn(mono);
        given(genreRepo.delete(mono.block())).willReturn(Mono.empty());

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
    }
}
