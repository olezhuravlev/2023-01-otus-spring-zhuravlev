package ru.otus.spring.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.otus.spring.configs.AppConfig;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.configs.PrintProps;
import ru.otus.spring.dao.AuthorDao;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.dao.GenreDao;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;
import ru.otus.spring.shell.CLICommands;
import ru.otus.spring.shell.CLIValueProvider;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("GLI commands test")
@JdbcTest
@Import({AppConfig.class, CLICommands.class, AuthorPrinter.class, GenrePrinter.class, BookPrinter.class, PrintProps.class})
public class CLICommandsTest {

    @MockBean
    private AuthorDao authorDao;

    @MockBean
    private GenreDao genreDao;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private AppProps appProps;

    @MockBean
    private CLIValueProvider cliValueProvider;

    @Autowired
    private PrintProps printProps;

    @Autowired
    @InjectMocks
    private CLICommands cliCommands;

    private static final List<Book> BOOKS_LIST = new ArrayList<>();

    @BeforeAll
    public static void before() {
        BOOKS_LIST.add(new Book(0, "Test book 0", "Test author 0", "Test genre 0"));
        BOOKS_LIST.add(new Book(1, "Test book 1", "Test author 1", "Test genre 1"));
        BOOKS_LIST.add(new Book(2, "Test book 2.1", "Test author 2", "Test genre 2"));
        BOOKS_LIST.add(new Book(3, "Test book 2.2", "Test author 2", "Test genre 2"));
    }

    @DisplayName("Get list of all authors")
    @Test
    public void getAuthors() {
        List<Author> expected = new ArrayList<>();
        expected.add(new Author(1, "Test author 1"));
        Mockito.when(authorDao.read()).thenReturn(expected);
        String result = cliCommands.getAuthors();
        assertEquals("""
                |------|----------------------------------------|
                |AUTHOR|NAME                                    |
                |------|----------------------------------------|
                |1     |Test author 1                           |
                |------|----------------------------------------|
                """, result);
    }

    @DisplayName("Get list of all genres")
    @Test
    public void getGenres() {
        List<Genre> expected = new ArrayList<>();
        expected.add(new Genre(1, "Test genre 1"));
        Mockito.when(genreDao.read()).thenReturn(expected);
        String result = cliCommands.getGenres();
        assertEquals("""
                |-----|----------------------------------------|
                |GENRE|NAME                                    |
                |-----|----------------------------------------|
                |1    |Test genre 1                            |
                |-----|----------------------------------------|
                """, result);
    }

    @DisplayName("Get list of all books")
    @Test
    public void getBooks() {
        Mockito.when(bookDao.read()).thenReturn(BOOKS_LIST);
        String result = cliCommands.getBook();
        assertEquals("""
                |-------|----------------------------------------|------------------------------|---------------|
                |BOOK ID|TITLE                                   |AUTHOR                        |GENRE          |
                |-------|----------------------------------------|------------------------------|---------------|
                |0      |Test book 0                             |Test author 0                 |Test genre 0   |
                |1      |Test book 1                             |Test author 1                 |Test genre 1   |
                |2      |Test book 2.1                           |Test author 2                 |Test genre 2   |
                |3      |Test book 2.2                           |Test author 2                 |Test genre 2   |
                |-------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Find book by ID")
    @Test
    public void getBookById() {

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookDao.read(captor.capture())).thenAnswer((Answer<Book>) invocation -> {
            Object[] args = invocation.getArguments();
            Long bookId = (Long) args[0];
            return BOOKS_LIST.get(bookId.intValue());
        });

        String result = cliCommands.getBook(1);
        assertEquals("""
                |-------|----------------------------------------|------------------------------|---------------|
                |BOOK ID|TITLE                                   |AUTHOR                        |GENRE          |
                |-------|----------------------------------------|------------------------------|---------------|
                |1      |Test book 1                             |Test author 1                 |Test genre 1   |
                |-------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Find book(s) by title")
    @Test
    public void getBookByTitle() {

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookDao.read(captor.capture())).thenAnswer((Answer<List<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String bookTitle = (String) args[0];
            return BOOKS_LIST.stream().filter(book -> book.getTitle().contains(bookTitle)).toList();
        });

        String result = cliCommands.getBook("book 2");
        assertEquals("""
                |-------|----------------------------------------|------------------------------|---------------|
                |BOOK ID|TITLE                                   |AUTHOR                        |GENRE          |
                |-------|----------------------------------------|------------------------------|---------------|
                |2      |Test book 2.1                           |Test author 2                 |Test genre 2   |
                |3      |Test book 2.2                           |Test author 2                 |Test genre 2   |
                |-------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Create book")
    @Test
    public void createBook() {
        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn("Dummy book")
                .thenReturn("10")
                .thenReturn("20");

        cliCommands.createBook();
        Mockito.verify(bookDao, Mockito.times(1)).create("Dummy book", 10L, 20L);
    }

    @DisplayName("Update book by ID")
    @Test
    public void updateBookById() {
        Mockito.when(bookDao.read(Mockito.anyLong())).thenReturn(BOOKS_LIST.get(1));
        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn("Dummy book")
                .thenReturn("10")
                .thenReturn("20");

        cliCommands.updateBook(1L);
        Mockito.verify(bookDao, Mockito.times(1)).update(1L, "Dummy book", 10L, 20L);
    }

    @DisplayName("Delete book by ID")
    @Test
    public void deleteBookById() {
        cliCommands.deleteBook(1L);
        Mockito.verify(bookDao, Mockito.times(1)).delete(1L);
    }
}
