package ru.otus.webapp.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.otus.shared.dto.AuthorDto;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.shared.dto.BookDto;
import ru.otus.shared.dto.GenreDto;
import ru.otus.webapp.config.AppProps;

import java.util.List;
import java.util.Map;

@Service
public class ApiGateImpl implements ApiGate {

    private final WebClient webClient;
    private final AppProps appProps;

    public ApiGateImpl(WebClient.Builder builder, AppProps appProps) {
        this.appProps = appProps;
        webClient = builder
                .baseUrl(appProps.restServerUri())
                .build();
    }

    @Override
    public List<AuthorDto> getAuthors() {

        List<AuthorDto> result = webClient.post().uri("/authors")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(AuthorDto.class)
                .toStream().toList();

        return result;
    }

    @Override
    public AuthorDto getAuthorById(String id) {

        try {
            AuthorDto result = webClient.post().uri(String.format("/authors/%s", id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(AuthorDto.class)
                    .block();
            return result;
        } catch (WebClientResponseException e) {
            return new AuthorDto();
        }
    }

    @Override
    public List<GenreDto> getGenres() {

        List<GenreDto> result = webClient.post().uri("/genres")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(GenreDto.class)
                .toStream().toList();

        return result;
    }

    @Override
    public GenreDto getGenreById(String id) {

        try {
            GenreDto result = webClient.post().uri(String.format("/genres/%s", id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(GenreDto.class)
                    .block();
            return result;
        } catch (WebClientResponseException e) {
            return new GenreDto();
        }
    }

    @Override
    public List<BookDto> getBooks() {
        List<BookDto> result = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("get", "all")
                        .build())
                .contentType(MediaType.APPLICATION_NDJSON)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(BookDto.class).collectList().block();
        return result;
    }

    @Override
    public BookDto getBook(String id) {
        try {
            BookDto result = webClient.post().uri(String.format("/books/%s", id))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(BookDto.class)
                    .block();
            return result;
        } catch (WebClientResponseException e) {
            return new BookDto(id, "Not found", "?", "?", "?", "?", null);
        }
    }

    @Override
    public BookDto saveBook(BookDto dto) {

        BookDto result = webClient.put().uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .build())
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(BookDto.class).block();

        return result;
    }

    @Override
    public Map<String, String> deleteBookById(String id) {

        Map<String, String> result = webClient.delete().uri(String.format("/books/%s", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return result;
    }

    @Override
    public BookCommentDto saveBookComment(BookCommentDto dto) {

        BookCommentDto result = webClient.put().uri(uriBuilder -> uriBuilder
                        .path("/comments")
                        .build())
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(BookCommentDto.class).block();

        return result;
    }

    @Override
    public Map<String, String> deleteBookCommentById(String id) {

        Map<String, String> result = webClient.delete().uri(String.format("/comments/%s", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return result;
    }

    @Override
    public List<BookCommentDto> getCommentsByBookId(String bookId) {

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path("/comments")
                        .pathSegment("book")
                        .pathSegment("{bookId}")
                        .queryParam("get")
                        .build(bookId))
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(BookCommentDto.class)
                .toStream().toList();
    }
}
