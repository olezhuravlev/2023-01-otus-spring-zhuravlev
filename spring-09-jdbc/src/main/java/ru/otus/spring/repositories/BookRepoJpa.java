package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookRepoJpa implements BookRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    private AppProps appProps;

    @Override
    public List<Book> findAll() {
        var query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        return query.getResultList();
    }

    @Override
    public Optional<Book> findById(long id) {
        Book result = entityManager.find(Book.class, id);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Book> findByTitle(String title) {

        var query = entityManager.createQuery("""
                SELECT DISTINCT b FROM Book b
                JOIN FETCH b.author a
                JOIN FETCH b.genre g
                WHERE LOWER(b.title) LIKE :title
                """, Book.class);
        query.setParameter("title", "%" + title.toLowerCase(appProps.locale()) + "%");

        return query.getResultList();
    }

    @Override
    public boolean isBookExist(long id) {
        var query = entityManager.find(Book.class, id);
        return query != null;
    }

    @Override
    public Book save(Book book) {

        if (book.getId() <= 0) {
            Author author = book.getAuthor();
            Author persistentAuthor = entityManager.merge(author);
            book.setAuthor(persistentAuthor);
            Genre genre = book.getGenre();
            Genre persistentGenre = entityManager.merge(genre);
            book.setGenre(persistentGenre);
            entityManager.persist(book);
            return book;
        } else {
            return entityManager.merge(book);
        }
    }

    @Override
    public void delete(Book book) {
        Book toRemove = entityManager.find(Book.class, book.getId());
        if (toRemove != null) {
            entityManager.remove(toRemove);
        }
    }
}
