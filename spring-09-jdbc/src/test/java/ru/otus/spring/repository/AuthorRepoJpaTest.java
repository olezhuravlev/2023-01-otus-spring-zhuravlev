package ru.otus.spring.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.spring.model.Author;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Authors")
@SpringBootTest
@Testcontainers
public class AuthorRepoJpaTest {

    private static final String DATABASE_NAME = "librarydb_test";
    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();

    @Autowired
    private AuthorRepo authorRepo;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));
    }

    @DisplayName("Retrieve all authors from DB")
    @Test
    @Transactional
    public void find() {
        List<Author> authors = authorRepo.findAll();
        assertThat(authors).containsExactlyInAnyOrderElementsOf(EXPECTED_AUTHORS);
    }

    @DisplayName("Retrieve author by ID")
    @Test
    @Transactional
    public void findById() {
        long authorId = 1;
        Optional<Author> author = authorRepo.findById(authorId);
        assertThat(author.get()).isEqualTo(EXPECTED_AUTHORS.get(0));
    }
}
