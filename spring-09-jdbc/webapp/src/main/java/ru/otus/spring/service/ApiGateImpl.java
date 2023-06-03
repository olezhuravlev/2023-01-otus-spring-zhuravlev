package ru.otus.spring.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiGateImpl implements ApiGate {

    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final BookRepo bookRepo;
    private final BookCommentRepo bookCommentRepo;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAuthors() {
        return authorRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getGenres() {
        return genreRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksWithAuthorAndGenre() {
        return bookRepo.findAllWithAuthorAndGenre();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(long id) {
        return bookRepo.findById(id);
    }

    @Override
    @Transactional
    public Book saveBook(BookDto dto) {

        long bookId = dto.getId();
        Book existingBook = null;
        if (bookRepo.isBookExist(bookId)) {
            existingBook = bookRepo.findById(bookId).get();
        }

        if (existingBook == null) {
            existingBook = new Book();
        }

        Author existingAuthor = entityManager.find(Author.class, dto.getAuthor().getId());
        Genre existingGenre = entityManager.find(Genre.class, dto.getGenre().getId());

        existingBook.setTitle(dto.getTitle());
        existingBook.setAuthor(existingAuthor);
        existingBook.setGenre(existingGenre);

        return bookRepo.save(existingBook);
    }

    @Override
    @Transactional
    public void deleteBookById(long id) {
        bookRepo.deleteById(id);
    }

    @Override
    @Transactional
    public BookComment saveBookComment(BookCommentDto dto) {

        long commentId = dto.getId();

        BookComment existingComment = null;
        if (bookCommentRepo.isBookCommentExist(commentId)) {
            existingComment = bookCommentRepo.findById(commentId).get();
        }

        if (existingComment == null) {
            existingComment = new BookComment();
        }

        existingComment.setBookId(dto.getBookId());
        existingComment.setText(dto.getText());

        return bookCommentRepo.save(existingComment);
    }

    @Override
    @Transactional
    public void deleteBookCommentById(long commentId) {
        bookCommentRepo.deleteCommentById(commentId);
    }
}
