package ru.otus.spring.testcontainers.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.repository.BookCommentRepo;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for BookComments")
public class BookCommentRepoJpaTest extends AbstractBaseContainer {

    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();

    @Autowired
    private BookCommentRepo bookCommentRepo;

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_COMMENTS.add(new BookComment(1, "Test book comment 1", 1));
        EXPECTED_COMMENTS.add(new BookComment(2, "Test book comment 2", 2));
        EXPECTED_COMMENTS.add(new BookComment(3, "Test book comment 3", 3));
    }

    @DisplayName("Retrieve book comment by ID")
    @Test
    void findById() {

        long bookCommentId = 1;

        Optional<BookComment> bookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(bookComment.get()).usingRecursiveComparison().isEqualTo(EXPECTED_COMMENTS.get(0));
    }

    @DisplayName("Check if book comment exists")
    @Test
    void isBookExist() {

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
    void save() {

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
    void delete() {

        long bookCommentId = 1;

        Optional<BookComment> existingBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(existingBookComment).isPresent();

        bookCommentRepo.deleteCommentById(bookCommentId);

        Optional<BookComment> deletedBookComment = bookCommentRepo.findById(bookCommentId);
        assertThat(deletedBookComment).isNotPresent();
    }
}
