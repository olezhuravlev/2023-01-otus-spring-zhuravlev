package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repositories.AuthorRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class CLICommands {

    private static final String ROWS_CREATED = "rows-created";
    private static final String ROWS_CHANGED = "rows-changed";
    private static final String ROWS_DELETED = "rows-deleted";

    private static final String BOOK_CREATED = "book-created";

    private static final String NO_SUCH_BOOK = "no-such-book";
    private static final String NO_SUCH_AUTHOR = "no-such-author";
    private static final String NO_SUCH_GENRE = "no-such-genre";
    private static final String ENTER_BOOK_TITLE = "enter-book-title";
    private static final String ENTER_AUTHOR_ID = "enter-author-id";
    private static final String ENTER_GENRE_ID = "enter-genre-id";

    private final AppProps appProps;

    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final BookRepo bookRepo;

    private final AuthorPrinter authorPrinter;
    private final GenrePrinter genrePrinter;
    private final BookPrinter bookPrinter;

    private final CLIValueProvider cliValueProvider;

    private final MessageSource messageSource;

    @ShellMethod(value = "get list of all authors", key = {"a", "aa", "get-all-authors"})
    public String getAuthors() {
        List<Author> authors = authorRepo.read();
        return authorPrinter.print(authors);
    }

    @ShellMethod(value = "get list of all genres", key = {"g", "ga", "get-all-genres"})
    public String getGenres() {
        List<Genre> genres = genreRepo.read();
        return genrePrinter.print(genres);
    }

    @ShellMethod(value = "get list of all books", key = {"ba", "get-all-books"})
    public String getBook() {
        List<Book> books = bookRepo.read();
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "find book by ID", key = {"b", "get-book"})
    public String getBook(long id) {
        var result = bookRepo.read(id);
        List toPrint;
        if (result.isPresent()) {
            toPrint = Arrays.asList(result.get());
        } else {
            toPrint = new ArrayList<>();
        }
        return bookPrinter.print(toPrint);
    }

    @ShellMethod(value = "find books by title", key = {"bt", "get-book-by-title"})
    public String getBook(String title) {
        List<Book> books = bookRepo.read(title);
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "create book", key = {"bc", "create-book"})
    public String createBook() {

        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText);

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long author_id = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingAuthor = authorRepo.read(author_id);
        if (existingAuthor.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_AUTHOR, null, appProps.locale());
        }

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long genre_id = Long.parseLong(cliValueProvider.getValue(welcomeText));
        var existingGenre = genreRepo.read(genre_id);
        if (existingGenre.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_GENRE, null, appProps.locale());
        }

        Book createdBook = bookRepo.create(title, existingAuthor.get(), existingGenre.get());
        return messageSource.getMessage(BOOK_CREATED, new String[]{createdBook.toString()}, appProps.locale());
    }

    @ShellMethod(value = "update book", key = {"bu", "update-book"})
    public String updateBook(long id) {

        var existingBook = bookRepo.read(id);
        if (existingBook.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        Book book = existingBook.get();
        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText + " (" + book.getTitle() + "):");

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long author_id = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getAuthor() + "):"));
        var existingAuthor = authorRepo.read(author_id);
        if (existingAuthor.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long genre_id = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getGenre() + "):"));
        var existingGenre = genreRepo.read(genre_id);
        if (existingGenre.isEmpty()) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        int result = bookRepo.update(id, title, existingAuthor.get(), existingGenre.get());
        return messageSource.getMessage(ROWS_CHANGED, new String[]{String.valueOf(result)}, appProps.locale());
    }

    @ShellMethod(value = "delete book", key = {"bd", "delete-book"})
    public String deleteBook(long id) {
        int result = bookRepo.delete(id);
        return messageSource.getMessage(ROWS_DELETED, new String[]{String.valueOf(result)}, appProps.locale());
    }
}
