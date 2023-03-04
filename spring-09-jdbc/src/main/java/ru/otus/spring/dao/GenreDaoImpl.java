package ru.otus.spring.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.mappers.GenreMapper;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class GenreDaoImpl implements GenreDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GenreMapper mapper;

    @Override
    public List<Genre> read() {
        List<Genre> genres = namedParameterJdbcTemplate.query("SELECT ID, NAME FROM GENRES", mapper);
        return genres;
    }
}
