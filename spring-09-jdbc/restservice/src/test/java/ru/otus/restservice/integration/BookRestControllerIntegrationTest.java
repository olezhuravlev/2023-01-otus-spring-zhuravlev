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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BookRestControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private BookCommentRepo bookCommentRepo;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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

    @BeforeEach
    public void beforeEach() {

        authorRepo.deleteAll()
                .then(authorRepo.save(TEST_ITEMS_AUTHORS.get(0)))
                .then(authorRepo.save(TEST_ITEMS_AUTHORS.get(1)))
                .then(authorRepo.save(TEST_ITEMS_AUTHORS.get(2)))
                .block();

        genreRepo.deleteAll()
                .then(genreRepo.save(TEST_ITEMS_GENRES.get(0)))
                .then(genreRepo.save(TEST_ITEMS_GENRES.get(1)))
                .then(genreRepo.save(TEST_ITEMS_GENRES.get(2)))
                .block();

        bookCommentRepo.deleteAll()
                .then(bookCommentRepo.save(TEST_ITEMS_COMMENTS.get(0)))
                .then(bookCommentRepo.save(TEST_ITEMS_COMMENTS.get(1)))
                .then(bookCommentRepo.save(TEST_ITEMS_COMMENTS.get(2)))
                .then(bookCommentRepo.save(TEST_ITEMS_COMMENTS.get(3)))
                .block();

        bookRepo.deleteAll()
                .then(bookRepo.save(TEST_ITEMS_BOOKS.get(0)))
                .then(bookRepo.save(TEST_ITEMS_BOOKS.get(1)))
                .then(bookRepo.save(TEST_ITEMS_BOOKS.get(2)))
                .then(bookRepo.save(TEST_ITEMS_BOOKS.get(3)))
                .block();
    }

    @DisplayName("Get all books")
    @Test
    void getAll() {

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

        Book source = TEST_ITEMS_BOOKS.stream().filter(book -> book.getId().equals(bookId)).findAny().orElse(null);
        BookDto result = BookDto.toDto(source);

        webTestClient
                .post().uri(String.format("/books/%s", bookId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(result));

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
        String commentId1 = "bc1";
        String commentId2 = "bc2";

        int dataLimit = TEST_ITEMS_BOOKS.size() * 2;
        List<BookDto> expectedResult = TEST_ITEMS_BOOKS_DTO.stream().filter(dto -> !dto.getId().equals(bookId)).toList();

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

        List<BookDto> bookResult = webTestClient
                .post().uri("/books")
                .contentType(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody()
                .take(dataLimit)
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();

        assertThat(bookResult).containsExactlyInAnyOrderElementsOf(expectedResult);

        // All comments of the removed book must be also deleted.
        webTestClient
                .post().uri(String.format("/comments/%s", commentId1))
                .exchange()
                .expectStatus().isNotFound();

        webTestClient
                .post().uri(String.format("/comments/%s", commentId2))
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("Find book by title ignoring case")
    @Test
    void findByTitleContainingIgnoreCase() {

        String bookTitle = "BOOK 1";

        List<Book> source = TEST_ITEMS_BOOKS.stream()
                .filter(book -> book.getTitle().toUpperCase().contains(bookTitle.toUpperCase())).toList();
        List<BookDto> sourceDto = source.stream().map(BookDto::toDto).toList();

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
}
