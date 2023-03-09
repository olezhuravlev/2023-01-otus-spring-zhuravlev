package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.mappers.GenreMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DAO for Genres")
@JdbcTest
@Import({GenreDaoImpl.class, GenreMapper.class})
public class GenreDaoImplTest {

    @Autowired
    private GenreDao genreDao;

    @DisplayName("Retrieve all genres from DB")
    @Test
    public void read() {
        List<Genre> expected = new ArrayList<>();
        expected.add(new Genre(1, "Test genre 1"));
        expected.add(new Genre(2, "Test genre 2"));
        expected.add(new Genre(3, "Test genre 3"));

        List<Genre> genres = genreDao.read();
        assertThat(genres).containsExactlyInAnyOrderElementsOf(expected);
    }
}
