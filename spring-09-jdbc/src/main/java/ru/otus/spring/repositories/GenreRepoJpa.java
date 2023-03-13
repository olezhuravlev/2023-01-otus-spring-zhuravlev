package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Genre;

import java.util.List;

@Repository
@AllArgsConstructor
public class GenreRepoJpa implements GenreRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Genre> read() {
        var query = entityManager.createQuery("SELECT g from Genre g", Genre.class);
        List<Genre> resultList = query.getResultList();
        return resultList;
    }
}
