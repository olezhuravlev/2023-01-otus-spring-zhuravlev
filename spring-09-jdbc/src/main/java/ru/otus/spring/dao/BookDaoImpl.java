package ru.otus.spring.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Book;
import ru.otus.spring.service.mappers.BookMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class BookDaoImpl implements BookDao {

    private final AppProps appProps;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final BookMapper mapper;

    @Override
    public List<Book> read() {
        List<Book> books = namedParameterJdbcTemplate.query("""
                SELECT b.id, b.TITLE BOOK, a.NAME AUTHOR, g.NAME GENRE FROM BOOKS b
                LEFT JOIN AUTHORS a ON b.ID_AUTHOR=a.ID
                LEFT JOIN GENRES g ON b.ID_GENRE=g.ID
                ORDER BY b.TITLE""", mapper);
        return books;
    }

    @Override
    public Book read(long id) {

        if (!isExist(id)) {
            return null;
        }

        Book book = namedParameterJdbcTemplate.queryForObject("""
                SELECT b.id, b.TITLE BOOK, a.NAME AUTHOR, g.NAME GENRE FROM BOOKS b
                LEFT JOIN AUTHORS a ON b.ID_AUTHOR=a.ID
                LEFT JOIN GENRES g ON b.ID_GENRE=g.ID
                WHERE b.ID = :id
                ORDER BY b.TITLE""", Map.of("id", id), mapper);
        return book;
    }

    @Override
    public List<Book> read(String title) {
        String titleCondition = appProps.dbLikeTemplate() + title + appProps.dbLikeTemplate();
        List<Book> books = namedParameterJdbcTemplate.query("""
                SELECT b.id, b.TITLE BOOK, a.NAME AUTHOR, g.NAME GENRE FROM BOOKS b
                LEFT JOIN AUTHORS a ON b.ID_AUTHOR=a.ID
                LEFT JOIN GENRES g ON b.ID_GENRE=g.ID
                WHERE b.TITLE LIKE :title
                ORDER BY b.TITLE""", Map.of("title", titleCondition), mapper);
        return books;
    }

    @Override
    public int create(String title, long id_author, long id_genre) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("title", title);
        paramMap.put("id_author", String.valueOf(id_author));
        paramMap.put("id_genre", String.valueOf(id_genre));
        return namedParameterJdbcTemplate.update("""
                INSERT INTO BOOKS (TITLE, ID_AUTHOR, ID_GENRE) values (:title, :id_author, :id_genre)
                """, paramMap);
    }

    @Override
    public int update(long id, String title, long id_author, long id_genre) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("id", String.valueOf(id));
        paramMap.put("title", title);
        paramMap.put("id_author", String.valueOf(id_author));
        paramMap.put("id_genre", String.valueOf(id_genre));
        return namedParameterJdbcTemplate.update("""
                UPDATE BOOKS SET TITLE=:title, ID_AUTHOR=:id_author, ID_GENRE=:id_genre WHERE id=:id
                """, paramMap);
    }

    @Override
    public int delete(long id) {
        return namedParameterJdbcTemplate.update("DELETE FROM BOOKS WHERE id=:id", Map.of("id", id));
    }

    private boolean isExist(long id) {
        int result = namedParameterJdbcTemplate.query("""
                SELECT TOP 1
                FROM BOOKS
                WHERE ID=:id;
                """, Map.of("id", id), rs -> rs.last() ? rs.getRow() : 0);
        return result > 0;
    }
}
