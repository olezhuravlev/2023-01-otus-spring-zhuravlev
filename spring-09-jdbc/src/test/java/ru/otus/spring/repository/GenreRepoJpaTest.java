package ru.otus.spring.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.spring.config.ApplicationTestConfig;
import ru.otus.spring.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Genres")
@DataMongoTest
@ContextConfiguration(classes = {ApplicationTestConfig.class})
public class GenreRepoJpaTest {

    @Autowired
    private GenreRepo genreRepo;

    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @BeforeAll
    public static void before() {
        EXPECTED_GENRES.add(new Genre("g1", "Test genre 1"));
        EXPECTED_GENRES.add(new Genre("g2", "Test genre 2"));
        EXPECTED_GENRES.add(new Genre("g3", "Test genre 3"));
    }

    @DisplayName("Retrieve all genres from DB")
    @Test
    public void find() {
        List<Genre> genres = genreRepo.findAll();
        assertThat(genres).containsExactlyInAnyOrderElementsOf(EXPECTED_GENRES);
    }

    @DisplayName("Retrieve genre by ID")
    @Test
    public void findById() {
        String genreId = "g1";
        Optional<Genre> genre = genreRepo.findById(genreId);
        assertThat(genre.get()).isEqualTo(EXPECTED_GENRES.get(0));
    }
}
