package ru.otus.spring.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for BookComments")
@SpringBootTest
@Testcontainers
public class BookCommentRepoJpaTest {

    private static final String DATABASE_NAME = "librarydb_test";

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();
    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();

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

    @BeforeAll
    public static void beforeAll() {

        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));

        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));

        EXPECTED_COMMENTS.add(new BookComment(1, "Test book comment 1", 1));
        EXPECTED_COMMENTS.add(new BookComment(2, "Test book comment 2", 2));
        EXPECTED_COMMENTS.add(new BookComment(3, "Test book comment 3", 3));

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), Arrays.asList(EXPECTED_COMMENTS.get(0))));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), Arrays.asList(EXPECTED_COMMENTS.get(1))));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), Arrays.asList(EXPECTED_COMMENTS.get(2))));
    }

    @DisplayName("Retrieve book comment by ID")
    @Test
    @Transactional
    public void findById() {

        long bookCommentId = 1;

        Optional<BookComment> bookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(bookComment.get()).usingRecursiveComparison().isEqualTo(EXPECTED_COMMENTS.get(0));
    }

    @DisplayName("Check if book comment exists")
    @Test
    @Transactional
    public void isBookExist() {

        long existingBookCommentId = 1;
        long absentBookCommentId = -1000;

        boolean existingBookComment = bookCommentRepo.isBookCommentExist(existingBookCommentId);
        assertThat(existingBookComment).isTrue();

        boolean absentBookComment = bookCommentRepo.isBookCommentExist(absentBookCommentId);
        assertThat(absentBookComment).isFalse();
    }

    @DisplayName("Save book comment")
    @Test
    @Transactional
    @DirtiesContext
    public void save() {

        long initialSequenceId = 1000;
        long bookId = 1;
        String text = "Test book new comment";

        BookComment newBookComment = new BookComment(initialSequenceId, text, bookId);
        BookComment saved = bookCommentRepo.save(newBookComment);

        Optional<BookComment> retrievedBookComment = bookCommentRepo.findById(saved.getId());
        assertThat(retrievedBookComment.get()).usingRecursiveComparison().isEqualTo(newBookComment);
    }

    @DisplayName("Delete book comment by ID")
    @Test
    @Transactional
    @DirtiesContext
    public void delete() {

        long bookCommentId = 1;

        Optional<BookComment> existingBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(existingBookComment.isPresent()).isTrue();

        bookCommentRepo.deleteCommentById(bookCommentId);

        Optional<BookComment> deletedBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(deletedBookComment.isEmpty()).isTrue();
    }
}
