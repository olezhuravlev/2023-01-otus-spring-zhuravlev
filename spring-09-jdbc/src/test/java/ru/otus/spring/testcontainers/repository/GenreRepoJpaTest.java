package ru.otus.spring.testcontainers.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repository.GenreRepo;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA for Genres")
public class GenreRepoJpaTest extends AbstractBaseContainer {

    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @Autowired
    private GenreRepo genreRepo;

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));
    }

    @DisplayName("Retrieve all genres from DB")
    @Test
    void find() {
        List<Genre> genres = genreRepo.findAll();
        assertThat(genres).containsExactlyInAnyOrderElementsOf(EXPECTED_GENRES);
    }

    @DisplayName("Retrieve genre by ID")
    @Test
    void findById() {
        long genreId = 1;
        Optional<Genre> genre = genreRepo.findById(genreId);
        assertThat(genre).contains(EXPECTED_GENRES.get(0));
    }
}
