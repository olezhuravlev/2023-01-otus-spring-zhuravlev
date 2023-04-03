package ru.otus.spring.repositories;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.spring.configs.AppConfig;
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
@DataMongoTest
@ContextConfiguration(classes = {AppConfig.class})
public class BookRepoJpaTest {

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private GenreRepo genreRepo;

    @Autowired
    private BookRepo bookRepo;

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();
    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();

    @BeforeAll
    public static void before() {

        EXPECTED_AUTHORS.add(new Author("a1", "Test author 1"));
        EXPECTED_AUTHORS.add(new Author("a2", "Test author 2"));
        EXPECTED_AUTHORS.add(new Author("a3", "Test author 3"));

        EXPECTED_GENRES.add(new Genre("g1", "Test genre 1"));
        EXPECTED_GENRES.add(new Genre("g2", "Test genre 2"));
        EXPECTED_GENRES.add(new Genre("g3", "Test genre 3"));

        EXPECTED_COMMENTS.add(new BookComment(new ObjectId("bc1111111111111111111111"), "Test book comment 1", "b1"));
        EXPECTED_COMMENTS.add(new BookComment(new ObjectId("bc2222222222222222222222"), "Test book comment 2", "b2"));
        EXPECTED_COMMENTS.add(new BookComment(new ObjectId("bc3333333333333333333333"), "Test book comment 3", "b3"));

        EXPECTED_BOOKS.add(new Book("b1", "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), Arrays.asList(EXPECTED_COMMENTS.get(0))));
        EXPECTED_BOOKS.add(new Book("b2", "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), Arrays.asList(EXPECTED_COMMENTS.get(1))));
        EXPECTED_BOOKS.add(new Book("b3", "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), Arrays.asList(EXPECTED_COMMENTS.get(2))));
    }

    @DisplayName("Retrieve all books from DB")
    @Test
    public void findAll() {
        List<Book> books = bookRepo.findAll();
        assertThat(books).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Retrieve book by ID")
    @Test
    public void findById() {

        String bookId = "b1";

        Optional<Book> book = bookRepo.findById(bookId);
        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(bookId);
    }

    @DisplayName("Retrieve book by title")
    @Test
    public void findByTitle() {

        String bookTitle = "book";

        List<Book> book = bookRepo.findByTitleContainingIgnoreCase(bookTitle);
        assertThat(book).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Save book")
    @Test
    @DirtiesContext
    public void save() {

        String bookId = "b4";

        Author author = EXPECTED_AUTHORS.get(0);
        var persistentAuthor = authorRepo.findById(author.getId());

        Genre genre = EXPECTED_GENRES.get(0);
        var persistentGenre = genreRepo.findById(genre.getId());

        Book newBook = new Book(bookId, "Test book 4", persistentAuthor.get(), persistentGenre.get(), new ArrayList<>());

        bookRepo.save(newBook);

        Optional<Book> savedBook = bookRepo.findById(newBook.getId());
        assertThat(savedBook.get()).usingRecursiveComparison().isEqualTo(newBook);
    }

    @DisplayName("Delete book")
    @Test
    @DirtiesContext
    public void delete() {

        String bookId = "b1";

        Optional<Book> existingBookBefore = bookRepo.findById(bookId);
        assertThat(existingBookBefore.isPresent()).isTrue();

        bookRepo.delete(existingBookBefore.get());

        Optional<Book> existingBookAfter = bookRepo.findById(bookId);
        assertThat(existingBookAfter.isPresent()).isFalse();
    }
}
