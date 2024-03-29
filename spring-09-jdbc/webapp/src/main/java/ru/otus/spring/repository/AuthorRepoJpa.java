package ru.otus.spring.repository;

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
    public List<Author> findAll() {
        var query = entityManager.createQuery("SELECT a from Author a", Author.class);
        return query.getResultList();
    }

    @Override
    public Optional<Author> findById(long id) {
        Author result = entityManager.find(Author.class, id);
        return Optional.ofNullable(result);
    }
}
