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
        assertThat(books).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Retrieve book by ID")
    @Test
    public void findById() {
        Optional<Book> book = bookRepo.find(1);
        assertThat(book.get()).usingRecursiveComparison().isEqualTo(EXPECTED_BOOKS.get(0));
    }

    @DisplayName("Retrieve book by title")
    @Test
    public void findByTitle() {
        List<Book> book = bookRepo.find("book");
        assertThat(book).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(EXPECTED_BOOKS);
    }

    @DisplayName("Save book")
    @Test
    public void save() {

        Book newBook = new Book(0L, "Test book 4", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), new ArrayList<>());
        bookRepo.save(newBook);

        entityManager.clear();

        Optional<Book> savedBook = bookRepo.find(newBook.getId());
        assertThat(savedBook.get()).usingRecursiveComparison().isEqualTo(newBook);
    }

    @DisplayName("Delete book")
    @Test
    public void delete() {

        long bookId = 1;

        Optional<Book> existingBookBefore = bookRepo.find(bookId);
        assertThat(existingBookBefore.get()).usingRecursiveComparison().isEqualTo(EXPECTED_BOOKS.get(0));

        bookRepo.delete(existingBookBefore.get());
        Optional<Book> existingBookAfter = bookRepo.find(bookId);
        assertThat(existingBookAfter.isEmpty());
    }

    @DisplayName("Get book comments")
    @Test
    public void getComments() {
        List<BookComment> bookComments = bookRepo.getComments(EXPECTED_BOOKS.get(0));
        assertThat(bookComments).usingRecursiveComparison().isEqualTo(EXPECTED_COMMENTS.get(0));
    }

    @DisplayName("Get certain book comment")
    @Test
    public void getComment() {
        Optional<BookComment> bookComment = bookRepo.getComment(EXPECTED_BOOKS.get(0), 1);
        assertThat(bookComment.get()).usingRecursiveComparison().isEqualTo(EXPECTED_COMMENTS.get(0).get(0));
    }

    @DisplayName("Create book comment")
    @Test
    public void createComment() {

        long bookId = 1;
        String text = "Test book comment 4";

        Book book = bookRepo.find(bookId).get();
        BookComment bookCommentNew = bookRepo.createComment(book, text);

        entityManager.clear();

        Optional<BookComment> bookCommentStored = bookRepo.getComment(book, bookCommentNew.getId());
        assertThat(bookCommentStored.get()).usingRecursiveComparison().isEqualTo(bookCommentNew);
    }

    @DisplayName("Update book comment")
    @Test
    public void updateComment() {

        long bookId = 1;
        String text = "Test book comment 4";

        Book book = bookRepo.find(bookId).get();
        BookComment bookComment = bookRepo.getComment(book, bookId).get();
        bookComment.setText(text);
        bookRepo.updateComment(bookComment, text);

        entityManager.clear();

        BookComment bookCommentAfter = bookRepo.getComment(book, bookId).get();
        assertThat(bookCommentAfter).usingRecursiveComparison().isEqualTo(bookComment);
    }

    @DisplayName("Delete book comment")
    @Test
    public void deleteComment() {

        long bookId = 1;

        Book book = bookRepo.find(bookId).get();
        BookComment bookComment = bookRepo.getComment(book, bookId).get();
        bookRepo.deleteComment(bookComment);

        entityManager.clear();

        Optional<BookComment> savedBookComment = bookRepo.getComment(book, bookComment.getId());
        assertThat(savedBookComment.isEmpty());
    }

    @DisplayName("Delete all book comments")
    @Test
    public void deleteAllComments() {

        long bookId = 1;

        Book book = bookRepo.find(bookId).get();
        bookRepo.deleteComments(book);

        entityManager.clear();

        Book savedBook = bookRepo.find(bookId).get();
        assertThat(savedBook.getBookComments().isEmpty());
    }
}
