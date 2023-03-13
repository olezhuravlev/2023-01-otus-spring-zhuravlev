package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Author;

import java.util.List;

@Repository
@AllArgsConstructor
public class AuthorRepoJpa implements AuthorRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Author> read() {
        var query = entityManager.createQuery("SELECT a from Author a", Author.class);
        List<Author> resultList = query.getResultList();
        return resultList;
    }
}
