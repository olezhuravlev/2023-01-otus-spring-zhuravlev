package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Author;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class AuthorRepoJpa implements AuthorRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Author> find() {
        var query = entityManager.createQuery("SELECT a from Author a", Author.class);
        List<Author> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Optional<Author> find(long id) {
        Author result = entityManager.find(Author.class, id);
        return Optional.ofNullable(result);
    }
}
