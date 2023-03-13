package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Book;

import java.util.List;

@Repository
@AllArgsConstructor
public class BookRepoJpa implements BookRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Book> read() {
        var query = entityManager.createQuery("SELECT b from Book b", Book.class);
        List<Book> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Book read(long id) {
        return null;
    }
    @Override
    public List<Book> read(String title) {
        return null;
    }
    @Override
    public int create(String title, long id_author, long id_genre) {
        return 0;
    }
    @Override
    public int update(long id, String title, long id_author, long id_genre) {
        return 0;
    }
    @Override
    public int delete(long id) {
        return 0;
    }
}
