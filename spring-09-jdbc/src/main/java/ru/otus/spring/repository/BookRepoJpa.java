package ru.otus.spring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Book;
import ru.otus.spring.service.AclPermissionService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookRepoJpa implements BookRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    private AclPermissionService aclPermissionService;

    @Override
    public List<Book> findAllWithAuthorAndGenre() {
        var query = entityManager.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class);
        query.setHint(EntityGraph.EntityGraphType.FETCH.getKey(), entityManager.getEntityGraph("book-author-genre"));
        return query.getResultList();
    }

    @Override
    public Optional<Book> findById(long id) {
        Book result = entityManager.find(Book.class, id);
        return Optional.ofNullable(result);
    }

    @Override
    public boolean isBookExist(long id) {
        var query = entityManager.find(Book.class, id);
        return query != null;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() <= 0) {
            entityManager.persist(book);
            // Only Admin allowed to create books, and granted with all permissions by default.
            assignAdminPermissions(book);
            return book;
        } else {
            return entityManager.merge(book);
        }
    }

    @Override
    public void deleteById(long id) {
        Book toRemove = entityManager.find(Book.class, id);
        if (toRemove != null) {
            entityManager.remove(toRemove);
        }
    }

    private void assignAdminPermissions(Book book) {
        Arrays.asList(
                        BasePermission.WRITE,
                        BasePermission.READ,
                        BasePermission.CREATE,
                        BasePermission.DELETE,
                        BasePermission.ADMINISTRATION)
                .forEach(permission -> aclPermissionService.addPermissionForUser(book, permission, SecurityContextHolder.getContext().getAuthentication()));
    }
}
