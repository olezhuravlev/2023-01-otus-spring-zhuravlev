package ru.otus.spring.testcontainers.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repository.AuthorRepo;
import ru.otus.spring.repository.BookRepo;
import ru.otus.spring.repository.GenreRepo;
import ru.otus.spring.testcontainers.AbstractBaseContainer;
import ru.otus.spring.testcontainers.WithMockAdmin;
import ru.otus.spring.testcontainers.WithMockNonAdmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Books")
public class BookRepoJpaTest extends AbstractBaseContainer {

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

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), singletonList(EXPECTED_COMMENTS.get(0))));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), singletonList(EXPECTED_COMMENTS.get(1))));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), singletonList(EXPECTED_COMMENTS.get(2))));
    }

    @DisplayName("Retrieve all books from DB by 'Admin' user")
    @Test
    @Transactional
    @WithMockAdmin
    void findAllBooksAdmin() {
        List<Book> books = bookRepo.findAllWithAuthorAndGenre();
        assertThat(books).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Retrieve all books from DB by non-admin user")
    @Test
    @Transactional
    @WithMockNonAdmin
    void findAllBooksNonAdmin() {
        List<Book> books = bookRepo.findAllWithAuthorAndGenre();
        List<Book> expected = Arrays.asList(EXPECTED_BOOKS.get(1), EXPECTED_BOOKS.get(2));
        assertThat(books).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @DisplayName("Retrieve book by ID by 'Admin' user")
    @Test
    @Transactional
    @WithMockAdmin
    void findBookByIdAdmin() {

        long bookId = 1;

        Book expectedBook = EXPECTED_BOOKS.stream().filter(book -> bookId == book.getId()).findFirst().orElse(null);
        Optional<Book> book = bookRepo.findById(bookId);
        assertThat(book).isPresent();

        Book foundBook = book.get();
        foundBook.setAuthor(Hibernate.unproxy(foundBook.getAuthor(), Author.class));
        foundBook.setGenre(Hibernate.unproxy(foundBook.getGenre(), Genre.class));
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("Retrieve book by ID by non-admin user")
    @Test
    @Transactional
    @WithMockNonAdmin
    void findBookByIdNonAdmin() {
        long bookId = 1;
        Assertions.assertThrows(AccessDeniedException.class, () -> bookRepo.findById(bookId));
    }

    @DisplayName("Check if book exists")
    @Test
    void isBookExist() {

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
    @WithMockAdmin
    void saveBook() {

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
    @WithMockAdmin
    void deleteBookById() {

        long bookId = 1;

        Optional<Book> existingBookBefore = bookRepo.findById(bookId);
        assertThat(existingBookBefore).isPresent();

        bookRepo.deleteById(bookId);

        Optional<Book> existingBookAfter = bookRepo.findById(bookId);
        assertThat(existingBookAfter).isNotPresent();
    }
}
