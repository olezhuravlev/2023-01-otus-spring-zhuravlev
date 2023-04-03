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
@Import({ApplicationConfig.class})
public class BookCommentRepoJpaTest {

    @Autowired
    private BookCommentRepo bookCommentRepo;

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

    @DisplayName("Retrieve book comment by ID")
    @Test
    public void findById() {

        long bookCommentId = 1;

        Optional<BookComment> bookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(bookComment.get()).usingRecursiveComparison().isEqualTo(EXPECTED_COMMENTS.get(0).get(0));
    }

    @DisplayName("Save book comment")
    @Test
    public void save() {

        long bookId = 1;
        String text = "Test book new comment";

        BookComment bookComment = new BookComment(0L, text, bookId);
        bookCommentRepo.save(bookComment);

        entityManager.flush();

        BookComment bookCommentAfter = bookCommentRepo.findById(bookComment.getId()).get();
        assertThat(bookCommentAfter).usingRecursiveComparison().isEqualTo(bookComment);
    }

    @DisplayName("Update book comment")
    @Test
    public void update() {

        long bookCommentId = 1;
        String text = "Test book updated comment";

        Optional<BookComment> bookComment = bookCommentRepo.findById(bookCommentId);
        BookComment bookCommentOriginal = bookComment.get();
        bookCommentOriginal.setText(text);
        bookCommentRepo.save(bookCommentOriginal);

        entityManager.flush();

        BookComment bookCommentAfter = bookCommentRepo.findById(bookCommentOriginal.getId()).get();
        assertThat(bookCommentAfter).usingRecursiveComparison().isEqualTo(bookCommentOriginal);
    }

    @DisplayName("Delete book comment")
    @Test
    public void delete() {

        long bookCommentId = 1;

        Optional<BookComment> existingBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(existingBookComment.isPresent()).isTrue();

        bookCommentRepo.deleteById(bookCommentId);

        entityManager.flush();

        Optional<BookComment> deletedBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(deletedBookComment.isEmpty()).isTrue();
    }

    @DisplayName("Delete all book comments")
    @Test
    public void deleteComments() {

        long bookId = 1;

        List<BookComment> bookComments = bookCommentRepo.findAll();
        int initialSize = bookComments.size();
        int result = bookCommentRepo.deleteByBookId(bookId);
        assertThat(result).isEqualTo(1);

        bookComments = bookCommentRepo.findAll();
        assertThat(bookComments.size()).isEqualTo(initialSize - 1);
    }
}
