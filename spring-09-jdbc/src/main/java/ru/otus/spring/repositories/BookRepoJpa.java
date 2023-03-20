package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
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
    public List<Book> findAll() {
        var query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint(EntityGraphType.FETCH.getKey(), entityManager.getEntityGraph("book-author-genre"));
        List<Book> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Optional<Book> find(long id) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(EntityGraphType.FETCH.getKey(), entityManager.getEntityGraph("book-author-genre"));
        Book result = entityManager.find(Book.class, id, properties);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Book> find(String title) {

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
    public Book save(Book book) {
        if (book.getId() <= 0) {
            entityManager.persist(book);
            return book;
        } else {
            return entityManager.merge(book);
        }
    }

    @Transactional
    @Override
    public void delete(Book book) {
        Book toRemove;
        if (!entityManager.contains(book)) {
            toRemove = entityManager.merge(book);
        } else {
            toRemove = book;
        }
        entityManager.remove(toRemove);
    }

    @Override
    public List<BookComment> getComments(Book book) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(EntityGraph.EntityGraphType.FETCH.getKey(), entityManager.getEntityGraph("book-bookComments"));
        Book bookAttached = entityManager.find(Book.class, book.getId(), properties);
        List<BookComment> bookComments = bookAttached.getBookComments();
        return bookComments;
    }

    @Override
    public Optional<BookComment> getComment(Book book, long commentId) {
        var query = entityManager.createQuery("""
                SELECT bc FROM BookComment bc
                WHERE bc.id=:comment_id AND bc.bookId=:book_id 
                """, BookComment.class);

        query.setParameter("book_id", book.getId());
        query.setParameter("comment_id", commentId);

        BookComment bookComment = null;
        List<BookComment> bookComments = query.getResultList();
        if (!bookComments.isEmpty()) {
            bookComment = bookComments.get(0);
        }

        return Optional.ofNullable(bookComment);
    }

    @Transactional
    @Override
    public BookComment createComment(Book book, String text) {
        BookComment bookComment = new BookComment(0L, text, book.getId());
        entityManager.persist(bookComment);
        return bookComment;
    }

    @Transactional
    @Override
    public int updateComment(BookComment bookComment, String text) {
        var query = entityManager.createQuery("""
                UPDATE BookComment bc
                SET bc.text=:text
                WHERE bc.id=:comment_id AND bc.bookId=:book_id
                """);

        query.setParameter("comment_id", bookComment.getId());
        query.setParameter("book_id", bookComment.getBookId());
        query.setParameter("text", text);

        return query.executeUpdate();
    }

    @Transactional
    @Override
    public int removeComment(BookComment bookComment) {
        var query = entityManager.createQuery("""
                DELETE FROM BookComment bc
                WHERE bc.id=:comment_id AND bc.bookId=:book_id
                """);

        query.setParameter("comment_id", bookComment.getId());
        query.setParameter("book_id", bookComment.getBookId());

        return query.executeUpdate();
    }

    @Transactional
    @Override
    public int cleanComments(Book book) {
        var query = entityManager.createQuery("""
                DELETE FROM BookComment bc
                WHERE bc.bookId=:book_id 
                """);
        query.setParameter("book_id", book.getId());

        return query.executeUpdate();
    }
}
