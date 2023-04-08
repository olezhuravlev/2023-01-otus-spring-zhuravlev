package ru.otus.spring.service;

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
import ru.otus.spring.repositories.BookRepoEager;
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
    BookRepoEager bookRepoEager;

    @Autowired
    BookCommentRepo bookCommentRepo;

    @Override
    public List<Author> getAuthors() {
        return authorRepo.findAll();
    }

    @Override
    public Optional<Author> getAuthor(long id) {
        return authorRepo.findById(id);
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepo.findAll();
    }

    @Override
    public Optional<Genre> getGenre(long id) {
        return genreRepo.findById(id);
    }

    @Override
    public List<Book> getBooksWithAuthorAndGenre() {
        return bookRepoEager.findAll();
    }

    @Override
    public Optional<Book> getBookById(long id) {
        return bookRepo.findById(id);
    }

    @Override
    public Optional<Book> getBookByIdWithAuthorAndGenre(long id) {
        return bookRepoEager.findById(id);
    }

    @Override
    public boolean isBookExist(long id) {
        return bookRepo.existsById(id);
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepoEager.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional
    public Book save(String title, long authorId, long genreId) {
        var existingAuthor = authorRepo.findById(authorId);
        var existingGenre = genreRepo.findById(genreId);
        Book book = new Book(0L, title, existingAuthor.get(), existingGenre.get(), new ArrayList<>());
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
    public List<BookComment> getCommentsByBook(Book book) {
        var persistentBook = bookRepoEager.findWithCommentsById(book.getId());
        return persistentBook.get().getBookComments();
    }

    @Override
    public Optional<BookComment> getBookComment(long commentID) {
        return bookCommentRepo.findById(commentID);
    }

    @Override
    public boolean isBookCommentExist(long id) {
        return bookCommentRepo.existsById(id);
    }

    @Override
    @Transactional
    public BookComment createBookComment(long bookId, String text) {
        BookComment bookComment = new BookComment(0L, text, bookId);
        return bookCommentRepo.save(bookComment);
    }

    @Override
    @Transactional
    public BookComment updateBookComment(long bookCommentId, String text) {
        Optional<BookComment> existingBookComment = bookCommentRepo.findById(bookCommentId);
        BookComment bookComment = existingBookComment.get();
        bookComment.setText(text);
        return bookCommentRepo.save(bookComment);
    }

    @Override
    @Transactional
    public void deleteBookCommentById(long commentId) {
        bookCommentRepo.deleteById(commentId);
    }

    @Override
    @Transactional
    public int deleteCommentsByBookId(long bookId) {
        return bookCommentRepo.deleteByBookId(bookId);
    }
}
