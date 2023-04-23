package ru.otus.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repository.AuthorRepo;
import ru.otus.spring.repository.BookCommentRepo;
import ru.otus.spring.repository.BookRepo;
import ru.otus.spring.repository.GenreRepo;

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
    public Optional<Book> getBook(String id) {
        return bookRepo.findById(id);
    }

    @Transactional
    @Override
    public Book saveBook(BookDto dto) {

        Optional<Author> existingAuthor = getAuthor(dto.getAuthor().getId());
        Optional<Genre> existingGenre = getGenre(dto.getGenre().getId());

        Optional<Book> existingBook = getBook(dto.getId());

        Book book;
        if (existingBook.isPresent()) {
            book = existingBook.get();
        } else {
            book = new Book();
            book.setBookComments(new ArrayList<>());
        }

        book.setTitle(dto.getTitle());
        book.setAuthor(existingAuthor.get());
        book.setGenre(existingGenre.get());

        return bookRepo.save(book);
    }

    @Override
    @Transactional
    public void deleteBookById(String bookId) {
        bookRepo.deleteById(bookId);
    }

    @Override
    public Optional<BookComment> getBookComment(String id) {
        return bookCommentRepo.findById(id);
    }

    @Override
    @Transactional
    public BookComment saveBookComment(BookCommentDto dto) {

        Optional<BookComment> existingBookComment = getBookComment(dto.getId());

        BookComment bookComment;
        if (existingBookComment.isPresent()) {
            bookComment = existingBookComment.get();
        } else {
            bookComment = new BookComment();
            bookComment.setBookId(dto.getBookId());
        }

        bookComment.setText(dto.getText());

        return bookCommentRepo.save(bookComment);
    }

    @Override
    @Transactional
    public void deleteBookCommentById(String commentId) {

        // To clean the comment out of a book we need 'bookId' kept in the comment entity.
        Optional<BookComment> existingBookComment = bookCommentRepo.findById(commentId);
        BookComment bookComment = existingBookComment.get();
        String bookId = bookComment.getBookId();

        Optional<Book> existingBook = bookRepo.findById(bookId);
        Book book = existingBook.get();
        book.deleteBookComment(commentId);
        bookRepo.save(book);

        bookCommentRepo.deleteById(commentId);
    }
}
