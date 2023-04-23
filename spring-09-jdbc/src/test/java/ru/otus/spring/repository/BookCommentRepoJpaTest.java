package ru.otus.spring.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.spring.config.ApplicationTestConfig;
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
@DataMongoTest
@ContextConfiguration(classes = {ApplicationTestConfig.class})
public class BookCommentRepoJpaTest {

    @Autowired
    private BookCommentRepo bookCommentRepo;

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

        EXPECTED_COMMENTS.add(new BookComment("bc1", "Test book comment 1", "b1"));
        EXPECTED_COMMENTS.add(new BookComment("bc2", "Test book comment 2", "b2"));
        EXPECTED_COMMENTS.add(new BookComment("bc3", "Test book comment 3", "b3"));

        EXPECTED_BOOKS.add(new Book("b1", "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), Arrays.asList(EXPECTED_COMMENTS.get(0))));
        EXPECTED_BOOKS.add(new Book("b2", "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), Arrays.asList(EXPECTED_COMMENTS.get(1))));
        EXPECTED_BOOKS.add(new Book("b3", "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), Arrays.asList(EXPECTED_COMMENTS.get(2))));
    }

    @DisplayName("Retrieve book comment by ID")
    @Test
    public void findById() {

        String bookCommentId = "bc1";

        Optional<BookComment> bookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(bookComment.get()).usingRecursiveComparison().isEqualTo(EXPECTED_COMMENTS.get(0));
    }

    @DisplayName("Save book comment")
    @Test
    @DirtiesContext
    public void save() {

        String bookCommentId = "bc4";
        String text = "Test book new comment";
        String bookId = "b1";

        BookComment bookComment = new BookComment(bookCommentId, text, bookId);
        bookCommentRepo.save(bookComment);

        BookComment bookCommentAfter = bookCommentRepo.findById(bookComment.getId().toString()).get();
        assertThat(bookCommentAfter).usingRecursiveComparison().isEqualTo(bookComment);
    }

    @DisplayName("Update book comment")
    @Test
    @DirtiesContext
    public void update() {

        String bookCommentId = "bc1";
        String text = "Test book updated comment";

        Optional<BookComment> bookComment = bookCommentRepo.findById(bookCommentId);
        BookComment bookCommentOriginal = bookComment.get();
        bookCommentOriginal.setText(text);
        bookCommentRepo.save(bookCommentOriginal);

        BookComment bookCommentAfter = bookCommentRepo.findById(bookCommentOriginal.getId().toString()).get();
        assertThat(bookCommentAfter).usingRecursiveComparison().isEqualTo(bookCommentOriginal);
    }

    @DisplayName("Delete book comment")
    @Test
    @DirtiesContext
    public void delete() {

        String bookCommentId = "bc1";

        Optional<BookComment> existingBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(existingBookComment.isPresent()).isTrue();

        bookCommentRepo.deleteById(bookCommentId);

        Optional<BookComment> deletedBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(deletedBookComment.isEmpty()).isTrue();
    }

    @DisplayName("Delete all book comments")
    @Test
    @DirtiesContext
    public void deleteComments() {

        String bookId = "b1";

        List<BookComment> bookComments = bookCommentRepo.findAll();
        int initialSize = bookComments.size();
        bookCommentRepo.deleteByBookId(bookId);

        bookComments = bookCommentRepo.findAll();
        assertThat(bookComments.size()).isEqualTo(initialSize - 1);

        List<BookComment> result = new ArrayList<>();
        result.add(EXPECTED_COMMENTS.get(1));
        result.add(EXPECTED_COMMENTS.get(2));
        assertThat(bookComments).usingRecursiveComparison().isEqualTo(result);
    }
}
