package ru.otus.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repositories.AuthorRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Facade {

    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final BookRepo bookRepo;

    public List<Author> getAuthors() {
        return authorRepo.find();
    }

    public Optional<Author> getAuthor(long id) {
        return authorRepo.find(id);
    }

    public List<Genre> getGenres() {
        return genreRepo.find();
    }

    public Optional<Genre> getGenre(long id) {
        return genreRepo.find(id);
    }

    public List<Book> getBooks() {
        return bookRepo.findAll();
    }

    public Optional<Book> getBook(long id) {
        return bookRepo.find(id);
    }

    public List<Book> findBooks(String title) {
        return bookRepo.find(title);
    }

    public Book save(Book book) {
        return bookRepo.save(book);
    }

    public void deleteBook(Book book) {
        bookRepo.delete(book);
    }

    public List<BookComment> getBookComments(Book book) {
        return bookRepo.getComments(book);
    }

    public Optional<BookComment> getBookComment(Book book, long commentID) {
        return bookRepo.getComment(book, commentID);
    }

    public BookComment createBookComment(Book book, String text) {
        return bookRepo.createComment(book, text);
    }

    public int updateBookComment(BookComment bookComment, String text) {
        return bookRepo.updateComment(bookComment, text);
    }

    public int deleteBookComment(BookComment bookComment) {
        return bookRepo.deleteComment(bookComment);
    }

    public int deleteBookComments(Book book) {
        return bookRepo.deleteComments(book);
    }
}
