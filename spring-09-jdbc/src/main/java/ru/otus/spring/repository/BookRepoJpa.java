package ru.otus.spring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookRepoJpa implements BookRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    protected MutableAclService mutableAclService;

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
            populate(book);
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

    @Override
    public void populate(Book book) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);

        ObjectIdentity oid = new ObjectIdentityImpl(book.getClass(), book.getId());
        final Sid admin = new GrantedAuthoritySid("ROLE_ADMIN");

        MutableAcl acl = mutableAclService.createAcl(oid);
        acl.setOwner(owner);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, admin, true);

        mutableAclService.updateAcl(acl);

        System.out.println("Done!");
    }
}
