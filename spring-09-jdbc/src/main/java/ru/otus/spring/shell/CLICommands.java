package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookCommentPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class CLICommands {

    private static final String ROWS_DELETED = "rows-deleted";
    private static final String BOOK_CREATED = "book-created";
    private static final String NO_SUCH_BOOK = "no-such-book";
    private static final String BOOK_SAVED = "book-saved";
    private static final String BOOK_DELETED = "book-deleted";
    private static final String NO_SUCH_AUTHOR = "no-such-author";
    private static final String NO_SUCH_GENRE = "no-such-genre";
    private static final String NO_SUCH_BOOK_COMMENT = "no-such-book-comment";
    private static final String ENTER_BOOK_TITLE = "enter-book-title";
    private static final String ENTER_BOOK_COMMENT_TEXT = "enter-book-comment-text";
    private static final String BOOK_COMMENT_CREATED = "book-comment-created";
    private static final String BOOK_COMMENT_DELETED = "book-comment-deleted";
    private static final String ENTER_AUTHOR_ID = "enter-author-id";
    private static final String ENTER_GENRE_ID = "enter-genre-id";

    private final ApiGate apiGate;

    private final AppProps appProps;

    private final MessageSource messageSource;

    private final CLIValueProvider cliValueProvider;

    private final AuthorPrinter authorPrinter;
    private final GenrePrinter genrePrinter;
    private final BookPrinter bookPrinter;
    private final BookCommentPrinter bookCommentPrinter;

    @ShellMethod(value = "get list of all authors", key = {"al", "authors-list"})
    public String getAuthorsList() {
        List<Author> authors = apiGate.getAuthors();
        return authorPrinter.print(authors);
    }

    @ShellMethod(value = "get list of all genres", key = {"gl", "genres-list"})
    public String getGenresList() {
        List<Genre> genres = apiGate.getGenres();
        return genrePrinter.print(genres);
    }

    @ShellMethod(value = "get list of all books", key = {"bl", "books-list"})
    public String getBooksList() {
        List<Book> books = apiGate.getBooks();
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "find book by ID", key = {"b", "book-find"})
    public String findBookById(long id) {
        var result = apiGate.getBookById(id);
        List<Book> toPrint = result.map(List::of).orElseGet(ArrayList::new);
        return bookPrinter.print(toPrint);
    }

    @ShellMethod(value = "find books by title", key = {"bt", "book-find-by-title"})
    public String findBooksByTitle(String title) {
        List<Book> books = apiGate.findBooksByTitle(title);
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "add book", key = {"ba", "book-add"})
    public String addBook() {

        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText);

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long authorId = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingAuthor = apiGate.getAuthor(authorId);
        if (existingAuthor.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_AUTHOR, null, appProps.locale());
        }

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long genreId = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingGenre = apiGate.getGenre(genreId);
        if (existingGenre.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_GENRE, null, appProps.locale());
        }

        Book createdBook = apiGate.save(title, authorId, genreId);
        return messageSource.getMessage(BOOK_CREATED, new String[]{createdBook.toString()}, appProps.locale());
    }

    @ShellMethod(value = "update book", key = {"bu", "book-update"})
    public String updateBook(long id) {

        var existingBook = apiGate.getBookById(id);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        Book book = existingBook.get();
        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText + " (" + book.getTitle() + "):");
        book.setTitle(title);

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long authorId = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getAuthor() + "):"));
        var existingAuthor = apiGate.getAuthor(authorId);
        if (existingAuthor.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        book.setAuthor(existingAuthor.get());

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long genreId = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getGenre() + "):"));
        var existingGenre = apiGate.getGenre(genreId);
        if (existingGenre.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }
        book.setGenre(existingGenre.get());

        Book updatedBook = apiGate.update(book);
        return messageSource.getMessage(BOOK_SAVED, new String[]{"\"" + updatedBook.getTitle() + "\""}, appProps.locale());
    }

    @ShellMethod(value = "delete book", key = {"bd", "book-delete"})
    public String deleteBook(long id) {

        var existingBook = apiGate.getBookById(id);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        String title = existingBook.get().getTitle();

        apiGate.deleteBook(existingBook.get());
        return messageSource.getMessage(BOOK_DELETED, new String[]{"\"" + title + "\""}, appProps.locale());
    }

    @ShellMethod(value = "list all comments of a specified book", key = {"bcl", "book-comments-list"})
    public String listBookComments(long bookId) {

        var existingBook = apiGate.getBookById(bookId);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        var bookComments = apiGate.getCommentsByBook(existingBook.get());
        return bookCommentPrinter.print(bookComments);
    }

    @ShellMethod(value = "find book comment by ID", key = {"bc", "book-comment-find"})
    public String findBookCommentById(long commentId) {

        var existingBookComment = apiGate.getBookComment(commentId);
        if (existingBookComment.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK_COMMENT, null, appProps.locale());
        }

        return bookCommentPrinter.print(Arrays.asList(existingBookComment.get()));
    }

    @ShellMethod(value = "add a comment to specified book", key = {"bca", "book-comment-add"})
    public String addBookComment(long bookId) {

        var isBookExist = apiGate.isBookExist(bookId);
        if (!isBookExist) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        String welcomeText = messageSource.getMessage(ENTER_BOOK_COMMENT_TEXT, null, appProps.locale());
        String commentText = cliValueProvider.getValue(welcomeText);
        apiGate.createBookComment(bookId, commentText);
        return messageSource.getMessage(BOOK_COMMENT_CREATED, null, appProps.locale());
    }

    @ShellMethod(value = "update specified book comment", key = {"bcu", "book-comment-update"})
    public String updateBookComment(long commentId) {

        var existingBookComment = apiGate.getBookComment(commentId);
        if (existingBookComment.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK_COMMENT, null, appProps.locale());
        }
        BookComment bookComment = existingBookComment.get();

        String welcomeText = messageSource.getMessage(ENTER_BOOK_COMMENT_TEXT, null, appProps.locale());
        String commentText = cliValueProvider.getValue(welcomeText + "(\"" + bookComment.getText() + "\"):");

        apiGate.updateBookComment(commentId, commentText);
        return messageSource.getMessage(BOOK_COMMENT_CREATED, null, appProps.locale());
    }

    @ShellMethod(value = "delete specified book comment", key = {"bcd", "book-comment-delete"})
    public String deleteBookComment(long commentId) {

        var isBookCommentExist = apiGate.isBookCommentExist(commentId);
        if (!isBookCommentExist) {
            return messageSource.getMessage(NO_SUCH_BOOK_COMMENT, null, appProps.locale());
        }

        apiGate.deleteBookCommentById(commentId);
        return messageSource.getMessage(BOOK_COMMENT_DELETED, null, appProps.locale());
    }

    @ShellMethod(value = "delete all comments of specified book", key = {"bcda", "book-comment-delete-all"})
    public String deleteAllBookComments(long bookId) {

        var isBookExist = apiGate.isBookExist(bookId);
        if (!isBookExist) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        int result = apiGate.deleteCommentsByBookId(bookId);
        return messageSource.getMessage(ROWS_DELETED, new String[]{String.valueOf(result)}, appProps.locale());
    }
}
