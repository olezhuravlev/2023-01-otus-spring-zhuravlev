package ru.otus.spring.repository;

import org.hibernate.Hibernate;
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

@DisplayName("JPA for Books")
@SpringBootTest
@Testcontainers
public class BookRepoJpaTest {

    private static final String DATABASE_NAME = "librarydb_test";

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();
    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private BookRepo bookRepo;

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

    @DisplayName("Retrieve all books from DB")
    @Test
    @Transactional
    public void findAll() {
        List<Book> books = bookRepo.findAllWithAuthorAndGenre();
        assertThat(books).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Retrieve book by ID")
    @Test
    @Transactional
    public void findById() {

        long bookId = 1;

        Book expectedBook = EXPECTED_BOOKS.stream().filter(book -> bookId == book.getId()).findFirst().orElse(null);
        Optional<Book> book = bookRepo.findById(bookId);
        assertThat(book.isPresent()).isTrue();

        Book foundBook = book.get();
        foundBook.setAuthor(Hibernate.unproxy(foundBook.getAuthor(), Author.class));
        foundBook.setGenre(Hibernate.unproxy(foundBook.getGenre(), Genre.class));
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Check if book exists")
    @Test
    @Transactional
    public void isBookExist() {

        long existingBookId = 1;
        long absentBookId = -1000;

        boolean existingBook = bookRepo.isBookExist(existingBookId);
        assertThat(existingBook).isTrue();

        boolean absentBook = bookRepo.isBookExist(absentBookId);
        assertThat(absentBook).isFalse();
    }

    @DisplayName("Save book")
    @Test
    @Transactional
    @DirtiesContext
    public void save() {

        // Initial sequence set to 1000.
        long initialSequenceId = 1000;

        Author author = EXPECTED_AUTHORS.get(0);
        Genre genre = EXPECTED_GENRES.get(0);

        var persistentAuthor = authorRepo.findById(author.getId());
        var persistentGenre = genreRepo.findById(genre.getId());

        Book newBook = new Book(initialSequenceId, "Test book 4", persistentAuthor.get(), persistentGenre.get(), new ArrayList<>());
        Book saved = bookRepo.save(newBook);

        // Saved book can have other ID, because table has initial sequence!
        Optional<Book> retrievedBook = bookRepo.findById(saved.getId());
        assertThat(retrievedBook.get()).usingRecursiveComparison().isEqualTo(newBook);
    }

    @DisplayName("Delete book by ID")
    @Test
    @Transactional
    @DirtiesContext
    public void delete() {

        long bookId = 1;

        Optional<Book> existingBookBefore = bookRepo.findById(bookId);
        assertThat(existingBookBefore.isPresent()).isTrue();

        bookRepo.deleteById(bookId);

        Optional<Book> existingBookAfter = bookRepo.findById(bookId);
        assertThat(existingBookAfter.isPresent()).isFalse();
    }
}
