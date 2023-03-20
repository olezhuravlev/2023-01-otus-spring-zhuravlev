package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repositories.AuthorRepo;
import ru.otus.spring.repositories.BookCommentRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookCommentPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class CLICommands {

    private static final String ROW_CHANGED = "row-changed";
    private static final String ROWS_CHANGED = "rows-changed";
    private static final String ROWS_DELETED = "rows-deleted";
    private static final String BOOK_CREATED = "book-created";
    private static final String NO_SUCH_BOOK = "no-such-book";
    private static final String BOOK_SAVED = "book-saved";
    private static final String BOOK_DELETED = "book-deleted";
    private static final String BOOK_NOT_DELETED = "book-not-deleted";
    private static final String NO_SUCH_AUTHOR = "no-such-author";
    private static final String NO_SUCH_GENRE = "no-such-genre";
    private static final String NO_SUCH_BOOK_COMMENT = "no-such-book-comment";
    private static final String ENTER_BOOK_TITLE = "enter-book-title";
    private static final String ENTER_BOOK_COMMENT_ID = "enter-book-comment-id";
    private static final String ENTER_BOOK_COMMENT_TEXT = "enter-book-comment-text";
    private static final String ENTER_AUTHOR_ID = "enter-author-id";
    private static final String ENTER_GENRE_ID = "enter-genre-id";

    private final AppProps appProps;

    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final BookRepo bookRepo;
    private final BookCommentRepo bookCommentRepo;

    private final AuthorPrinter authorPrinter;
    private final GenrePrinter genrePrinter;
    private final BookPrinter bookPrinter;
    private final BookCommentPrinter bookCommentPrinter;

    private final CLIValueProvider cliValueProvider;

    private final MessageSource messageSource;

    @ShellMethod(value = "get list of all authors", key = {"al", "authors-list"})
    public String getAuthors() {
        List<Author> authors = authorRepo.read();
        return authorPrinter.print(authors);
    }

    @ShellMethod(value = "get list of all genres", key = {"gl", "genres-list"})
    public String getGenres() {
        List<Genre> genres = genreRepo.read();
        return genrePrinter.print(genres);
    }

    @ShellMethod(value = "get list of all books", key = {"bl", "books-list"})
    public String getBook() {
        List<Book> books = bookRepo.findAll();
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "find book by ID", key = {"b", "find-book"})
    public String getBook(long id) {
        var result = bookRepo.find(id);
        List<Book> toPrint = result.map(List::of).orElseGet(ArrayList::new);
        return bookPrinter.print(toPrint);
    }

    @ShellMethod(value = "find books by title", key = {"bt", "find-books-by-title"})
    public String getBook(String title) {
        List<Book> books = bookRepo.find(title);
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "add book", key = {"ba", "book-add"})
    public String addBook() {

        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText);

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long authorId = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingAuthor = authorRepo.read(authorId);
        if (existingAuthor.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_AUTHOR, null, appProps.locale());
        }

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long genreId = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingGenre = genreRepo.read(genreId);
        if (existingGenre.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_GENRE, null, appProps.locale());
        }

        Book createdBook = bookRepo.create(title, existingAuthor.get(), existingGenre.get());
        return messageSource.getMessage(BOOK_CREATED, new String[]{createdBook.toString()}, appProps.locale());
    }

    @ShellMethod(value = "update book", key = {"bu", "book-update"})
    public String updateBook(long id) {

        var existingBook = bookRepo.find(id);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        Book book = existingBook.get();
        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText + " (" + book.getTitle() + "):");
        book.setTitle(title);

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long authorId = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getAuthor() + "):"));
        var existingAuthor = authorRepo.read(authorId);
        if (existingAuthor.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        book.setAuthor(existingAuthor.get());

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long genreId = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getGenre() + "):"));
        var existingGenre = genreRepo.read(genreId);
        if (existingGenre.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        book.setGenre(existingGenre.get());

        Book savedBook = bookRepo.save(book);
        return messageSource.getMessage(BOOK_SAVED, new String[]{"\"" + savedBook.getTitle() + "\""}, appProps.locale());
    }

    @ShellMethod(value = "delete book", key = {"bd", "book-delete"})
    public String deleteBook(long id) {
        var existingBook = bookRepo.find(id);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        String title = existingBook.get().getTitle();

        bookRepo.remove(existingBook.get());

        existingBook = bookRepo.find(id);
        String messageId = BOOK_DELETED;
        if (!existingBook.isEmpty()) {
            messageId = BOOK_NOT_DELETED;
        }

        return messageSource.getMessage(messageId, new String[]{"\"" + title + "\""}, appProps.locale());
    }

    @ShellMethod(value = "list all comments of a specified book", key = {"bcl", "book-comments-list"})
    public String listBookComments(long bookId) {
        var existingBook = bookRepo.find(bookId);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        var bookComments = bookRepo.findComments(bookId);
        return bookCommentPrinter.print(bookComments);
    }

    @ShellMethod(value = "add comment for a specified book", key = {"bca", "book-comment-add"})
    public String addBookComment(long bookId) {

        var existingBook = bookRepo.find(bookId);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        Book book = existingBook.get();

        String welcomeText = messageSource.getMessage(ENTER_BOOK_COMMENT_TEXT, null, appProps.locale());
        String commentText = cliValueProvider.getValue(welcomeText);
        BookComment result = bookCommentRepo.create(book, commentText);
        return messageSource.getMessage(ROW_CHANGED, new String[]{String.valueOf(result)}, appProps.locale());
    }

    @ShellMethod(value = "update comment of specified book", key = {"bcu", "book-comment-update"})
    public String updateBookComment(long bookId) {

        var existingBook = bookRepo.find(bookId);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        Book book = existingBook.get();

        String welcomeText = messageSource.getMessage(ENTER_BOOK_COMMENT_ID, null, appProps.locale());
        long commentId = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingBookComment = bookCommentRepo.find(book, commentId);
        if (existingBookComment.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK_COMMENT, null, appProps.locale());
        }
        BookComment bookComment = existingBookComment.get();

        welcomeText = messageSource.getMessage(ENTER_BOOK_COMMENT_TEXT, null, appProps.locale());
        String commentText = cliValueProvider.getValue(welcomeText + "(\"" + bookComment.getText() + "\"):");

        BookComment result = bookCommentRepo.update(book, bookComment, commentText);
        return messageSource.getMessage(ROW_CHANGED, new String[]{String.valueOf(result)}, appProps.locale());
    }

    @ShellMethod(value = "delete comment of specified book", key = {"bcd", "book-comment-delete"})
    public String deleteBookComment(long bookId) {

        var existingBook = bookRepo.find(bookId);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        Book book = existingBook.get();

        String welcomeText = messageSource.getMessage(ENTER_BOOK_COMMENT_ID, null, appProps.locale());
        long commentId = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingBookComment = bookCommentRepo.find(book, commentId);
        if (existingBookComment.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK_COMMENT, null, appProps.locale());
        }
        BookComment bookComment = existingBookComment.get();

        Book result = bookCommentRepo.remove(book, bookComment);
        return messageSource.getMessage(ROW_CHANGED, new String[]{String.valueOf(result)}, appProps.locale());
    }

    @ShellMethod(value = "delete all comments of specified book", key = {"bcda", "book-comment-delete-all"})
    public String deleteAllBookComments(long bookId) {

        var existingBook = bookRepo.find(bookId);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        Book book = existingBook.get();

        Book result = bookCommentRepo.removeAll(book);
        return messageSource.getMessage(ROW_CHANGED, new String[]{String.valueOf(result)}, appProps.locale());
    }
}
