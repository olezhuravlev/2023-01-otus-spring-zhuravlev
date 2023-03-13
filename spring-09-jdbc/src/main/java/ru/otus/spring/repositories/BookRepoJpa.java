package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

@Repository
@AllArgsConstructor
public class BookRepoJpa implements BookRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    private AppProps appProps;

    @Override
    public List<Book> read() {
        var query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint(EntityGraphType.FETCH.getKey(), entityManager.getEntityGraph("book-eg"));
        List<Book> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Optional<Book> read(long id) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(EntityGraphType.FETCH.getKey(), entityManager.getEntityGraph("book-eg"));
        Book result = entityManager.find(Book.class, id, properties);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Book> read(String title) {

        var query = entityManager.createQuery("""
                SELECT DISTINCT b FROM Book b
                JOIN FETCH b.author a
                JOIN FETCH b.genre g
                WHERE LOWER(b.title) LIKE :title
                """, Book.class);
        query.setParameter("title", "%" + title.toLowerCase(appProps.locale()) + "%");

        return query.getResultList();
    }

    @Transactional
    @Override
    public Book create(String title, Author author, Genre genre) {

        Author authorAttached = entityManager.merge(author);
        Genre genreAttached = entityManager.merge(genre);
        var book = new Book(0L, title, authorAttached, genreAttached, null);

        entityManager.persist(book);

        return book;
    }

    @Transactional
    @Override
    public int update(long id, String title, Author author, Genre genre) {

        var query = entityManager.createQuery("""
                UPDATE Book b
                SET b.title=:title, b.author=:author, b.genre=:genre
                WHERE b.id=:id
                """);
        query.setParameter("title", title);
        query.setParameter("author", author);
        query.setParameter("genre", genre);
        query.setParameter("id", id);

        return query.executeUpdate();
    }

    @Transactional
    @Override
    public int delete(long id) {

        var query = entityManager.createQuery("""
                DELETE FROM Book b
                WHERE b.id=:id
                """);
        query.setParameter("id", String.valueOf(id));

        return query.executeUpdate();
    }
}
