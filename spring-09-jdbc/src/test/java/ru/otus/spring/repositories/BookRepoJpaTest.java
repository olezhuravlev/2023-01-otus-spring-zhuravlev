package ru.otus.spring.repositories;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.spring.configs.ApplicationConfig;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Books")
@DataJpaTest
@Import({ApplicationConfig.class, BookRepoJpa.class})
public class BookRepoJpaTest {

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private TestEntityManager entityManager;

    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();
    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<List<BookComment>> EXPECTED_COMMENTS = new ArrayList<>();

    @BeforeAll
    public static void before() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));

        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));

        EXPECTED_COMMENTS.add(List.of(new BookComment(1, "Test book comment 1", 1)));
        EXPECTED_COMMENTS.add(List.of(new BookComment(2, "Test book comment 2", 2)));
        EXPECTED_COMMENTS.add(List.of(new BookComment(3, "Test book comment 3", 3)));

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), EXPECTED_COMMENTS.get(0)));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), EXPECTED_COMMENTS.get(1)));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), EXPECTED_COMMENTS.get(2)));
    }

    @DisplayName("Retrieve all books from DB")
    @Test
    public void findAll() {
        List<Book> books = bookRepo.findAll();
        assertThat(books.size()).isEqualTo(3);
    }

    @DisplayName("Retrieve book by ID")
    @Test
    public void findById() {

        long bookId = 1;

        Optional<Book> book = bookRepo.findById(bookId);
        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(bookId);
    }

    @DisplayName("Retrieve book by title")
    @Test
    public void findByTitle() {

        String bookTitle = "book";

        List<Book> book = bookRepo.findByTitle(bookTitle);
        assertThat(book).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Save book")
    @Test
    public void save() {

        Book newBook = new Book(0L, "Test book 4", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), new ArrayList<>());
        bookRepo.save(newBook);

        entityManager.flush();

        Optional<Book> savedBook = bookRepo.findById(newBook.getId());
        assertThat(savedBook.get()).usingRecursiveComparison().isEqualTo(newBook);
    }

    @DisplayName("Delete book")
    @Test
    public void delete() {

        long bookId = 1;

        Optional<Book> existingBookBefore = bookRepo.findById(bookId);
        assertThat(existingBookBefore.isPresent()).isTrue();

        bookRepo.delete(existingBookBefore.get());
        Optional<Book> existingBookAfter = bookRepo.findById(bookId);
        assertThat(existingBookAfter.isPresent()).isFalse();
    }
}
