package ru.otus.spring.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Author;
import ru.otus.spring.service.mappers.AuthorMapper;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class AuthorDaoImpl implements AuthorDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final AuthorMapper mapper;

    @Override
    public List<Author> read() {
        List<Author> authors = namedParameterJdbcTemplate.query("SELECT ID, NAME FROM AUTHORS ", mapper);
        return authors;
    }
}
