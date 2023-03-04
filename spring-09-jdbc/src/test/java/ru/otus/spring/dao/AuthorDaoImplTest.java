package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.model.Author;
import ru.otus.spring.service.mappers.AuthorMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DAO for Authors")
@JdbcTest
@Import({AuthorDaoImpl.class, AuthorMapper.class})
public class AuthorDaoImplTest {

    @Autowired
    private AuthorDao authorDao;

    @DisplayName("Retrieve all authors from DB")
    @Test
    public void read() {
        List<Author> expected = new ArrayList<>();
        expected.add(new Author(1, "Test author 1"));
        expected.add(new Author(2, "Test author 2"));
        expected.add(new Author(3, "Test author 3"));

        List<Author> authors = authorDao.read();
        assertThat(authors).containsExactlyInAnyOrderElementsOf(expected);
    }
}
