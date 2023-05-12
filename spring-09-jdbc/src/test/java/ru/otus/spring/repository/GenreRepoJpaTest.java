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
import ru.otus.spring.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Genres")
@SpringBootTest
@Testcontainers
public class GenreRepoJpaTest {

    private static final String DATABASE_NAME = "librarydb_test";
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @Autowired
    private GenreRepo genreRepo;

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
        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));
    }

    @DisplayName("Retrieve all genres from DB")
    @Test
    @Transactional
    public void find() {
        List<Genre> genres = genreRepo.findAll();
        assertThat(genres).containsExactlyInAnyOrderElementsOf(EXPECTED_GENRES);
    }

    @DisplayName("Retrieve genre by ID")
    @Test
    @Transactional
    public void findById() {
        long genreId = 1;
        Optional<Genre> genre = genreRepo.findById(genreId);
        assertThat(genre.get()).isEqualTo(EXPECTED_GENRES.get(0));
    }
}
