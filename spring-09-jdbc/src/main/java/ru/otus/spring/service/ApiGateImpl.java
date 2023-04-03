package ru.otus.spring.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApiGateImpl implements ApiGate {

    @Autowired
    AuthorRepo authorRepo;

    @Autowired
    GenreRepo genreRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    BookCommentRepo bookCommentRepo;

    @Override
    public List<Author> getAuthors() {
        return authorRepo.findAll();
    }

    @Override
    public Optional<Author> getAuthor(String id) {
        return authorRepo.findById(id);
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepo.findAll();
    }

    @Override
    public Optional<Genre> getGenre(String id) {
        return genreRepo.findById(id);
    }

    @Override
    public List<Book> getBooks() {
        return bookRepo.findAll();
    }

    @Override
    public Optional<Book> getBookById(String id) {
        return bookRepo.findById(id);
    }

    @Override
    public boolean isBookExist(String id) {
        return bookRepo.existsById(id);
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepo.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional
    public Book save(String title, String authorId, String genreId) {
        var existingAuthor = authorRepo.findById(authorId);
        var existingGenre = genreRepo.findById(genreId);
        Book book = new Book(title, existingAuthor.get(), existingGenre.get(), new ArrayList<>());
        return bookRepo.save(book);
    }

    @Override
    @Transactional
    public Book update(Book book) {
        return bookRepo.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Book book) {
        bookRepo.delete(book);
    }

    @Override
    public List<BookComment> getBookComments(Book book) {

        Optional<Book> existingBook = bookRepo.findById(book.getId());
        if (existingBook.isEmpty()) {
            return new ArrayList<>();
        }

        return existingBook.get().getBookComments();
    }

    @Override
    public Optional<BookComment> getBookComment(String commentId) {
        return bookCommentRepo.findById(commentId);
    }

    @Override
    public boolean isBookCommentExist(String commentId) {
        return bookCommentRepo.existsById(commentId);
    }

    @Override
    @Transactional
    public BookComment createBookComment(String bookId, String text) {

        Optional<Book> existingBook = bookRepo.findById(bookId);
        Book book = existingBook.get();
        BookComment bookComment = new BookComment(new ObjectId(), text, bookId);
        book.addBookComment(bookComment);
        bookRepo.save(book);

        // In order not to overload server we save only the new created comment.
        return bookCommentRepo.save(bookComment);
    }

    @Override
    @Transactional
    public BookComment updateBookComment(String commentId, String text) {
        Optional<BookComment> existingBookComment = bookCommentRepo.findById(commentId);
        BookComment bookComment = existingBookComment.get();
        bookComment.setText(text);
        return bookCommentRepo.save(bookComment);
    }

    @Override
    @Transactional
    public void deleteBookCommentById(String commentId) {

        // To clean the comment out of a book we need bookId kept in the comment entity.
        Optional<BookComment> existingBookComment = bookCommentRepo.findById(commentId);
        BookComment bookComment = existingBookComment.get();
        String bookId = bookComment.getBookId();

        Optional<Book> existingBook = bookRepo.findById(bookId);
        Book book = existingBook.get();
        book.deleteBookComment(commentId);
        bookRepo.save(book);

        bookCommentRepo.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentsByBookId(String bookId) {
        Optional<Book> existingBook = bookRepo.findById(bookId);
        Book book = existingBook.get();
        book.setBookComments(new ArrayList<>());
        bookRepo.save(book);
    }
}
