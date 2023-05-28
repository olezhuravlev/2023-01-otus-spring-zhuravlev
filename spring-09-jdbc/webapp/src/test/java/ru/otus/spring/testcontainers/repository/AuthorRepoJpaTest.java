package ru.otus.spring.testcontainers.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.spring.model.Author;
import ru.otus.spring.repository.AuthorRepo;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Authors")
public class AuthorRepoJpaTest extends AbstractBaseContainer {

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();

    @Autowired
    private AuthorRepo authorRepo;

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));
    }

    @DisplayName("Retrieve all authors from DB")
    @Test
    void findAllAuthors() {
        List<Author> authors = authorRepo.findAll();
        assertThat(authors).containsExactlyInAnyOrderElementsOf(EXPECTED_AUTHORS);
    }

    @DisplayName("Retrieve author by ID")
    @Test
    void findAuthorById() {
        long authorId = 1;
        Optional<Author> author = authorRepo.findById(authorId);
        assertThat(author).contains(EXPECTED_AUTHORS.get(0));
    }
}
