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

        EXPECTED_COMMENTS.add(List.of(new BookComment(1, "Test book comment 1")));
        EXPECTED_COMMENTS.add(List.of(new BookComment(2, "Test book comment 2")));
        EXPECTED_COMMENTS.add(List.of(new BookComment(3, "Test book comment 3")));

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), EXPECTED_COMMENTS.get(0)));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), EXPECTED_COMMENTS.get(1)));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), EXPECTED_COMMENTS.get(2)));
    }

    @DisplayName("Retrieve all books from DB")
    @Test
    public void read() {
        List<Book> books = bookRepo.findAll();
        assertThat(books).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Retrieve book by ID")
    @Test
    public void readById() {
        Optional<Book> book = bookRepo.find(1);
        assertThat(book.get()).usingRecursiveComparison().isEqualTo(EXPECTED_BOOKS.get(0));
    }

    @DisplayName("Retrieve book by title")
    @Test
    public void readByTitle() {
        List<Book> book = bookRepo.find("book");
        assertThat(book).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Create book")
    @Test
    public void create() {
        Book book = bookRepo.create("Test book 4", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0));
        Optional<Book> existingBook = bookRepo.find(book.getId());
        assertThat(existingBook.get()).usingRecursiveComparison().isEqualTo(book);
    }

//    @DisplayName("Update book")
//    @Test
//    public void update() {
//
//        long bookId = 1;
//        String bookTitle = "New test title";
//
//        int result = bookRepo.save(bookId, bookTitle, EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(2));
//        assertThat(result).isEqualTo(1);
//
//        Optional<Book> existingBook = bookRepo.read(bookId);
//        Book exampleBook = new Book(bookId, bookTitle, EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(2), EXPECTED_COMMENTS.get(0));
//        assertThat(existingBook.get()).usingRecursiveComparison().isEqualTo(exampleBook);
//    }

    @DisplayName("Delete book")
    @Test
    public void delete() {

        long bookId = 1;

        Optional<Book> existingBookBefore = bookRepo.find(bookId);
        assertThat(existingBookBefore.get()).usingRecursiveComparison().isEqualTo(EXPECTED_BOOKS.get(0));

        bookRepo.remove(existingBookBefore.get());
        Optional<Book> existingBookAfter = bookRepo.find(bookId);
        assertThat(existingBookAfter.isEmpty());
    }
}
