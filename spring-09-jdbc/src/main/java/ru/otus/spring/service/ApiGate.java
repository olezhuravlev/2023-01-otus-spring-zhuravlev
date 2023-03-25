package ru.otus.spring.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repositories.AuthorRepo;
import ru.otus.spring.repositories.BookCommentRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiGate {

    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final BookRepo bookRepo;
    private final BookCommentRepo bookCommentRepo;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Author> getAuthors() {
        return authorRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Author> getAuthor(long id) {
        return authorRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Genre> getGenres() {
        return genreRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Genre> getGenre(long id) {
        return genreRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooks() {
        return bookRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookById(long id) {
        return bookRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean isBookExist(long id) {
        return bookRepo.isBookExist(id);
    }

    @Transactional(readOnly = true)
    public List<Book> findBooksByTitle(String title) {
        return bookRepo.findByTitle(title);
    }

    @Transactional
    public Book save(String title, long authorId, long genreId) {
        var existingAuthor = entityManager.find(Author.class, authorId);
        var existingGenre = entityManager.find(Genre.class, genreId);
        Book book = new Book(0L, title, existingAuthor, existingGenre, new ArrayList<>());
        return bookRepo.save(book);
    }

    @Transactional
    public Book update(Book book) {
        return bookRepo.save(book);
    }

    @Transactional
    public void deleteBook(Book book) {
        bookRepo.delete(book);
    }

    @Transactional(readOnly = true)
    public List<BookComment> getCommentsByBook(Book book) {
        Book persistentBook = entityManager.merge(book);
        List<BookComment> bookComments = persistentBook.getBookComments();
        return bookComments;
    }

    @Transactional(readOnly = true)
    public Optional<BookComment> getBookComment(long commentID) {
        return bookCommentRepo.findById(commentID);
    }

    @Transactional(readOnly = true)
    public boolean isBookCommentExist(long id) {
        return bookCommentRepo.isBookCommentExist(id);
    }

    @Transactional
    public BookComment createBookComment(long bookId, String text) {
        BookComment bookComment = new BookComment(0L, text, bookId);
        return bookCommentRepo.save(bookComment);
    }

    @Transactional
    public BookComment updateBookComment(long bookCommentId, String text) {
        Optional<BookComment> existingBookComment = bookCommentRepo.findById(bookCommentId);
        BookComment bookComment = existingBookComment.get();
        bookComment.setText(text);
        return bookCommentRepo.save(bookComment);
    }

    @Transactional
    public void deleteBookCommentById(long commentId) {
        bookCommentRepo.deleteCommentById(commentId);
    }

    @Transactional
    public int deleteCommentsByBookId(long bookId) {
        return bookCommentRepo.deleteCommentsByBookId(bookId);
    }
}
