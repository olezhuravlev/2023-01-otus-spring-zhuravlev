package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.model.BookComment;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookCommentRepoJpa implements BookCommentRepo {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<BookComment> findById(long commentId) {

        var query = entityManager.createQuery("""
                SELECT bc FROM BookComment bc
                WHERE bc.id=:commentId
                """, BookComment.class);
        query.setParameter("commentId", commentId);

        BookComment bookComment = null;
        List<BookComment> bookComments = query.getResultList();
        if (!bookComments.isEmpty()) {
            bookComment = bookComments.get(0);
        }

        return Optional.ofNullable(bookComment);
    }

    @Override
    public BookComment save(BookComment bookComment) {
        if (bookComment.getId() <= 0) {
            entityManager.persist(bookComment);
            return bookComment;
        } else {
            return entityManager.merge(bookComment);
        }
    }

    @Override
    public void deleteCommentById(long commentId) {
        BookComment toRemove = entityManager.find(BookComment.class, commentId);
        if (toRemove != null) {
            entityManager.remove(toRemove);
        }
    }

    @Override
    public boolean isBookCommentExist(long id) {
        var result = entityManager.find(BookComment.class, id);
        return result != null;
    }

    @Override
    public int deleteCommentsByBookId(long bookId) {

        var query = entityManager.createQuery("""
                DELETE FROM BookComment bc
                WHERE bc.bookId=:bookId
                """);
        query.setParameter("bookId", bookId);

        return query.executeUpdate();
    }
}
