package ru.otus.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

//@Repository
//@AllArgsConstructor
public class BookCommentRepoJpa /*implements BookCommentRepo*/ {
//
//    @PersistenceContext
//    private final EntityManager entityManager;
//
//    @Override
//    public Optional<BookComment> find(Book book, long commentId) {
//
//        var query = entityManager.createQuery("""
//                SELECT DISTINCT b FROM Book b
//                JOIN FETCH b.bookComments bc
//                WHERE b.id=:book_id AND bc.id=:comment_id
//                """, Book.class);
//
//        query.setParameter("book_id", book.getId());
//        query.setParameter("comment_id", commentId);
//
//        List<Book> storedBooks = query.getResultList();
//        BookComment foundComment = null;
//        if (!storedBooks.isEmpty()) {
//            List<BookComment> bookComments = storedBooks.get(0).getBookComments();
//            if (!bookComments.isEmpty()) {
//                foundComment = bookComments.get(0);
//            }
//        }
//
//        return Optional.ofNullable(foundComment);
//    }
//
//    @Transactional
//    @Override
//    public BookComment create(Book book, String text) {
//
//        Book bookAttached = entityManager.merge(book);
//        List<BookComment> bookComments = bookAttached.getBookComments();
//        BookComment bookComment = new BookComment(0L, text);
//        bookComments.add(bookComment);
//
//        entityManager.persist(bookAttached);
//
//        return bookComment;
//    }
//
//    @Transactional
//    @Override
//    public BookComment update(Book book, BookComment bookComment, String text) {
//
//        Book bookAttached = entityManager.merge(book);
//        List<BookComment> bookComments = bookAttached.getBookComments();
//        Optional<BookComment> existingBookComment = bookComments.stream()
//                .filter(comment -> comment.getId() == bookComment.getId())
//                .findFirst();
//
//        BookComment foundComment = null;
//        if (existingBookComment.isPresent()) {
//            foundComment = existingBookComment.get();
//            foundComment.setText(text);
//            entityManager.persist(bookAttached);
//        }
//
//        return foundComment;
//    }
//
//    @Transactional
//    @Override
//    public Book remove(Book book, BookComment bookComment) {
//
//        Book bookAttached = entityManager.merge(book);
//        List<BookComment> bookComments = bookAttached.getBookComments();
//
//        // To make deletion works we have to remove BookComment itself AND remove it from Book entity.
//        BookComment bookCommentAttached = entityManager.merge(bookComment);
//        entityManager.remove(bookCommentAttached);
//        bookComments.removeIf(comment -> comment.getId() == bookComment.getId());
//
//        return bookAttached;
//    }
//
//    @Transactional
//    @Override
//    public Book removeAll(Book book) {
//
//        Book bookAttached = entityManager.merge(book);
//        List<BookComment> bookComments = bookAttached.getBookComments();
//        if (bookComments.isEmpty()) {
//            return bookAttached;
//        }
//
//        // To make deletion works we have to remove BookComment itself AND remove it from Book entity.
//        for (BookComment bookComment : bookComments) {
//            entityManager.remove(bookComment);
//        }
//        bookComments.clear();
//
//        entityManager.persist(bookAttached);
//
//        return bookAttached;
//    }
}
