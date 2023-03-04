package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.dao.AuthorDao;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.dao.GenreDao;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;

import java.util.Arrays;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class CLICommands {

    private static final String ROWS_CREATED = "rows-created";
    private static final String ROWS_CHANGED = "rows-changed";
    private static final String ROWS_DELETED = "rows-deleted";

    private static final String NO_SUCH_BOOK = "no-such-book";
    private static final String ENTER_BOOK_TITLE = "enter-book-title";
    private static final String ENTER_AUTHOR_ID = "enter-author-id";
    private static final String ENTER_GENRE_ID = "enter-genre-id";

    private final AppProps appProps;

    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final BookDao bookDao;

    private final AuthorPrinter authorPrinter;
    private final GenrePrinter genrePrinter;
    private final BookPrinter bookPrinter;

    private final CLIValueProvider cliValueProvider;

    private final MessageSource messageSource;

    @ShellMethod(value = "get list of all authors", key = {"a", "aa", "get-all-authors"})
    public String getAuthors() {
        List<Author> authors = authorDao.read();
        return authorPrinter.print(authors);
    }

    @ShellMethod(value = "get list of all genres", key = {"g", "ga", "get-all-genres"})
    public String getGenres() {
        List<Genre> genres = genreDao.read();
        return genrePrinter.print(genres);
    }

    @ShellMethod(value = "get list of all books", key = {"ba", "get-all-books"})
    public String getBook() {
        List<Book> books = bookDao.read();
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "find book by ID", key = {"b", "get-book"})
    public String getBook(long id) {
        Book book = bookDao.read(id);
        return bookPrinter.print(Arrays.asList(book));
    }

    @ShellMethod(value = "find books by title", key = {"bt", "get-book-by-title"})
    public String getBook(String title) {
        List<Book> books = bookDao.read(title);
        return bookPrinter.print(books);
    }

    @ShellMethod(value = "create book", key = {"bc", "create-book"})
    public String createBook() {

        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText);

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long id_author = Long.parseLong(cliValueProvider.getValue(welcomeText));

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long id_genre = Long.parseLong(cliValueProvider.getValue(welcomeText));

        int result = bookDao.create(title, id_author, id_genre);
        return messageSource.getMessage(ROWS_CREATED, new String[]{String.valueOf(result)}, appProps.locale());
    }

    @ShellMethod(value = "update book", key = {"bu", "update-book"})
    public String updateBook(long id) {

        Book book = bookDao.read(id);
        if (book == null) {
            return messageSource.getMessage(NO_SUCH_BOOK, null, appProps.locale());
        }

        String welcomeText = messageSource.getMessage(ENTER_BOOK_TITLE, null, appProps.locale());
        String title = cliValueProvider.getValue(welcomeText + " (" + book.getTitle() + "):");

        welcomeText = messageSource.getMessage(ENTER_AUTHOR_ID, null, appProps.locale());
        long id_author = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getAuthor() + "):"));

        welcomeText = messageSource.getMessage(ENTER_GENRE_ID, null, appProps.locale());
        long id_genre = Long.parseLong(cliValueProvider.getValue(welcomeText + " (" + book.getGenre() + "):"));

        int result = bookDao.update(id, title, id_author, id_genre);
        return messageSource.getMessage(ROWS_CHANGED, new String[]{String.valueOf(result)}, appProps.locale());
    }

    @ShellMethod(value = "delete book", key = {"bd", "delete-book"})
    public String deleteBook(long id) {
        int result = bookDao.delete(id);
        return messageSource.getMessage(ROWS_DELETED, new String[]{String.valueOf(result)}, appProps.locale());
    }
}
