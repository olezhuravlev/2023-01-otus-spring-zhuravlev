package ru.otus.spring.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.spring.config.ApplicationTestConfig;
import ru.otus.spring.model.Author;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Authors")
@DataMongoTest
@ContextConfiguration(classes = {ApplicationTestConfig.class})
public class AuthorRepoJpaTest {

    @Autowired
    private AuthorRepo authorRepo;

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();

    @BeforeAll
    public static void before() {
        EXPECTED_AUTHORS.add(new Author("a1", "Test author 1"));
        EXPECTED_AUTHORS.add(new Author("a2", "Test author 2"));
        EXPECTED_AUTHORS.add(new Author("a3", "Test author 3"));
    }

    @DisplayName("Retrieve all authors from DB")
    @Test
    public void find() {
        List<Author> authors = authorRepo.findAll();
        assertThat(authors).containsExactlyInAnyOrderElementsOf(EXPECTED_AUTHORS);
    }

    @DisplayName("Retrieve author by ID")
    @Test
    public void findById() {
        String authorId = "a1";
        Optional<Author> author = authorRepo.findById(authorId);
        assertThat(author.get()).isEqualTo(EXPECTED_AUTHORS.get(0));
    }
}
