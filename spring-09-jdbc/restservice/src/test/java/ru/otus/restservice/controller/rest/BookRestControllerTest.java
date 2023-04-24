package ru.otus.restservice.controller.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.restservice.repository.AuthorRepo;
import ru.otus.restservice.repository.BookCommentRepo;
import ru.otus.restservice.repository.BookRepo;
import ru.otus.restservice.repository.GenreRepo;
import ru.otus.shared.dto.BookDto;
import ru.otus.shared.model.Author;
import ru.otus.shared.model.Book;
import ru.otus.shared.model.BookComment;
import ru.otus.shared.model.Genre;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@WebMvcTest(BookRestController.class)
public class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorRepo authorRepo;

    @MockBean
    private GenreRepo genreRepo;

    @MockBean
    private BookRepo bookRepo;

    @MockBean
    private BookCommentRepo bookCommentRepo;

    private static final List<Author> TEST_ITEMS_AUTHORS = new ArrayList<>();
    private static final List<Genre> TEST_ITEMS_GENRES = new ArrayList<>();
    private static final List<Book> TEST_ITEMS_BOOKS = new ArrayList<>();
    private static final List<BookComment> TEST_ITEMS_COMMENTS = new ArrayList<>();

    private static final List<BookDto> TEST_ITEMS_BOOKS_DTO = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {

        Author author1 = new Author("a1", "Test author 1");
        Author author2 = new Author("a2", "Test author 2");
        Author author3 = new Author("a3", "Test author 3");
        TEST_ITEMS_AUTHORS.addAll(Arrays.asList(author1, author2, author3));

        Genre genre1 = new Genre("g1", "Test genre 1");
        Genre genre2 = new Genre("g2", "Test genre 2");
        Genre genre3 = new Genre("g3", "Test genre 3");
        TEST_ITEMS_GENRES.addAll(Arrays.asList(genre1, genre2, genre3));

        BookComment comment1 = new BookComment("bc1", "b1", "Book comment 1");
        BookComment comment2 = new BookComment("bc2", "b1", "Book comment 2");
        BookComment comment3 = new BookComment("bc3", "b2", "Book comment 3");
        BookComment comment4 = new BookComment("bc4", "b2", "Book comment 4");
        TEST_ITEMS_COMMENTS.addAll(Arrays.asList(comment1, comment2, comment3, comment4));

        Book book1 = new Book("b1", "Test book 1", "a1", "Test author 1", "g1", "Test genre 1");
        Book book2 = new Book("b2", "Test book 2", "a1", "Test author 1", "g2", "Test genre 2");
        Book book3 = new Book("b3", "Test book 3", "a2", "Test author 2", "g2", "Test genre 2");
        Book book11 = new Book("b11", "Test book 11", "a1", "Test author 1", "g1", "Test genre 1");
        TEST_ITEMS_BOOKS.addAll(Arrays.asList(book1, book2, book3, book11));

        TEST_ITEMS_BOOKS_DTO.addAll(TEST_ITEMS_BOOKS.stream().map(BookDto::toDto).toList());
        Collections.reverse(TEST_ITEMS_BOOKS_DTO);
    }

    @DisplayName("Get all books")
    @Test
    void getAll() {

        when(authorRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Author>>) invocation -> findAuthor((String) invocation.getArguments()[0]));
        when(genreRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Genre>>) invocation -> findGenre((String) invocation.getArguments()[0]));

        Flux<Book> allBooksFlux = Flux.fromIterable(TEST_ITEMS_BOOKS);
        given(bookRepo.findAll()).willReturn(allBooksFlux);

        int dataLimit = TEST_ITEMS_BOOKS.size() * 2;
        List<BookDto> requestResult = webTestClient
                .post().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(requestResult).containsExactlyInAnyOrderElementsOf(TEST_ITEMS_BOOKS_DTO);
    }

    @DisplayName("Get book by ID")
    @Test
    void getById() {

        String bookId = "b1";
        String notFoundBookId = "b100";

        when(authorRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Author>>) invocation -> findAuthor((String) invocation.getArguments()[0]));
        when(genreRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Genre>>) invocation -> findGenre((String) invocation.getArguments()[0]));

        Book source = TEST_ITEMS_BOOKS.stream().filter(book -> book.getId().equals(bookId)).findAny().orElse(null);
        BookDto result = BookDto.toDto(source);

        Mono<Book> bookMono = Mono.just(source);
        given(bookRepo.findById(bookId)).willReturn(bookMono);

        webTestClient
                .post().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));

        given(bookRepo.findById(notFoundBookId)).willReturn(Mono.empty());
        webTestClient
                .post().uri(String.format("/books/%s", notFoundBookId))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Save book")
    @Test
    void save() {

        Book source = TEST_ITEMS_BOOKS.get(0);
        BookDto result = BookDto.toDto(source);

        Mono<Book> mono = Mono.just(source);
        given(bookRepo.save(source)).willReturn(mono);
        when(authorRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Author>>) invocation -> findAuthor((String) invocation.getArguments()[0]));
        when(genreRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Genre>>) invocation -> findGenre((String) invocation.getArguments()[0]));

        webTestClient
                .put().uri("/books")
                .bodyValue(source)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));
    }

    @DisplayName("Delete book by ID")
    @Test
    void deleteById() {

        String bookId = "b1";

        Book source = TEST_ITEMS_BOOKS.stream().filter(book -> book.getId().equals(bookId)).findAny().orElse(null);

        Mono<Book> mono = Mono.just(source);
        given(bookRepo.findById(bookId)).willReturn(mono);
        given(bookRepo.delete(mono.block())).willReturn(Mono.empty());
        given(bookCommentRepo.deleteByBookId(bookId)).willReturn(Mono.empty());

        webTestClient
                .delete().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Map.class)
                .consumeWith(response -> {
                    Map<String, String> map = response.getResponseBody();
                    assertThat(map.getOrDefault("id", null)).isEqualTo(bookId);
                    assertThat(map.getOrDefault("result", null)).isEqualTo("ok");
                });
    }

    @DisplayName("Find book by title ignoring case")
    @Test
    void findByTitleContainingIgnoreCase() {

        String bookTitle = "BOOK 1";

        when(authorRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Author>>) invocation -> findAuthor((String) invocation.getArguments()[0]));
        when(genreRepo.findById(anyString()))
                .thenAnswer((Answer<Mono<Genre>>) invocation -> findGenre((String) invocation.getArguments()[0]));

        List<Book> source = TEST_ITEMS_BOOKS.stream()
                .filter(book -> book.getTitle().toUpperCase().contains(bookTitle.toUpperCase())).toList();
        List<BookDto> sourceDto = source.stream().map(BookDto::toDto).toList();

        Flux<Book> booksFlux = Flux.fromIterable(source);
        given(bookRepo.findByTitleContainingIgnoreCase(bookTitle)).willReturn(booksFlux);

        int dataLimit = sourceDto.size() * 2;
        List<BookDto> requestResult = webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("like", bookTitle)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(requestResult).containsExactlyInAnyOrderElementsOf(sourceDto);
    }

    // SERVICE FUNCTIONS
    private Mono<Author> findAuthor(String id) {
        Author result = TEST_ITEMS_AUTHORS.stream()
                .filter(author -> author.getId().equals(id)).findAny().orElse(null);
        return Mono.just(result);
    }

    private Mono<Genre> findGenre(String id) {
        Genre result = TEST_ITEMS_GENRES.stream()
                .filter(genre -> genre.getId().equals(id)).findAny().orElse(null);
        return Mono.just(result);
    }
}
