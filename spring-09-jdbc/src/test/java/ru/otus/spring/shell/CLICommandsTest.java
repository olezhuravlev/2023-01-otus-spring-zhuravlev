package ru.otus.spring.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.otus.spring.configs.AppConfig;
import ru.otus.spring.configs.AppProps;
import ru.otus.spring.configs.PrintProps;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repositories.AuthorRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;
import ru.otus.spring.service.Facade;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookCommentPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("CLI test")
@DataJpaTest
@Import(value = {AppConfig.class, CLICommands.class, Facade.class, AuthorPrinter.class, GenrePrinter.class, BookPrinter.class, BookCommentPrinter.class, PrintProps.class})
public class CLICommandsTest {

    @MockBean
    private AuthorRepo authorRepo;

    @MockBean
    private GenreRepo genreRepo;

    @MockBean
    private BookRepo bookRepo;

    @MockBean
    private AppProps appProps;

    @MockBean
    private CLIValueProvider cliValueProvider;

    @Autowired
    private PrintProps printProps;

    @Autowired
    @InjectMocks
    private CLICommands cliCommands;

    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();
    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<List<BookComment>> EXPECTED_COMMENTS = new ArrayList<>();

    @BeforeEach
    public void before() {

        EXPECTED_AUTHORS.clear();
        EXPECTED_GENRES.clear();
        EXPECTED_COMMENTS.clear();
        EXPECTED_BOOKS.clear();

        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));

        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));

        EXPECTED_COMMENTS.add(List.of(new BookComment(1, "Test book comment 1", 1)));
        EXPECTED_COMMENTS.add(List.of(new BookComment(2, "Test book comment 2", 2)));
        EXPECTED_COMMENTS.add(List.of(new BookComment(3, "Test book comment 3", 3)));

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), EXPECTED_COMMENTS.get(0)));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), EXPECTED_COMMENTS.get(1)));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), EXPECTED_COMMENTS.get(2)));
    }

    @DisplayName("Get list of all authors")
    @Test
    public void getAuthorsList() {
        Mockito.when(authorRepo.find()).thenReturn(EXPECTED_AUTHORS);
        String result = cliCommands.getAuthorsList();
        assertEquals("""
                |------|----------------------------------------|
                |AUTHOR|NAME                                    |
                |------|----------------------------------------|
                |1     |Test author 1                           |
                |2     |Test author 2                           |
                |3     |Test author 3                           |
                |------|----------------------------------------|
                """, result);
    }

    @DisplayName("Get list of all genres")
    @Test
    public void getGenresList() {
        Mockito.when(genreRepo.find()).thenReturn(EXPECTED_GENRES);
        String result = cliCommands.getGenresList();
        assertEquals("""
                |-----|----------------------------------------|
                |GENRE|NAME                                    |
                |-----|----------------------------------------|
                |1    |Test genre 1                            |
                |2    |Test genre 2                            |
                |3    |Test genre 3                            |
                |-----|----------------------------------------|
                """, result);
    }

    @DisplayName("Get list of all books")
    @Test
    public void getBooksList() {

        Mockito.when(bookRepo.findAll()).thenReturn(EXPECTED_BOOKS);
        String result = cliCommands.getBooksList();
        assertEquals("""
                |-------|----------------------------------------|------------------------------|---------------|
                |BOOK ID|TITLE                                   |AUTHOR                        |GENRE          |
                |-------|----------------------------------------|------------------------------|---------------|
                |1      |Test book 1                             |Test author 1                 |Test genre 1   |
                |2      |Test book 2                             |Test author 2                 |Test genre 2   |
                |3      |Test book 3                             |Test author 3                 |Test genre 3   |
                |-------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Find book by ID")
    @Test
    public void findBookById() {

        long bookId = 1L;

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        String result = cliCommands.findBookById(bookId);
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
    public void findBooksByTitle() {

        String bookTitle = "book";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.find(stringCaptor.capture())).thenAnswer((Answer<List<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            return EXPECTED_BOOKS.stream().filter(book -> book.getTitle().contains(arg)).toList();
        });

        String result = cliCommands.findBooksByTitle(bookTitle);
        assertEquals("""
                |-------|----------------------------------------|------------------------------|---------------|
                |BOOK ID|TITLE                                   |AUTHOR                        |GENRE          |
                |-------|----------------------------------------|------------------------------|---------------|
                |1      |Test book 1                             |Test author 1                 |Test genre 1   |
                |2      |Test book 2                             |Test author 2                 |Test genre 2   |
                |3      |Test book 3                             |Test author 3                 |Test genre 3   |
                |-------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Add new book")
    @Test
    public void addBook() {

        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn("Dummy book")
                .thenReturn("1")
                .thenReturn("2");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.save(bookCaptor.capture())).thenAnswer((Answer<Book>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            return book;
        });

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(authorRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Author>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_AUTHORS.get(idx));
        });

        Mockito.when(genreRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Genre>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_GENRES.get(idx));
        });

        cliCommands.addBook();
        Mockito.verify(bookRepo, Mockito.times(1)).save(new Book(0L, "Dummy book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(1), new ArrayList<>()));
    }

    @DisplayName("Update book identified by ID")
    @Test
    public void updateBookById() {

        long bookId = 1L;

        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn("Dummy book")
                .thenReturn("1")
                .thenReturn("2");

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        Mockito.when(authorRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Author>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_AUTHORS.get(idx));
        });

        Mockito.when(genreRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Genre>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_GENRES.get(idx));
        });

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.save(bookCaptor.capture())).thenAnswer((Answer<Book>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            return book;
        });

        cliCommands.updateBook(bookId);
        Mockito.verify(bookRepo, Mockito.times(1)).save(new Book(bookId, "Dummy book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(1), EXPECTED_COMMENTS.get(0)));
    }

    @DisplayName("Delete book by ID")
    @Test
    public void deleteBookById() {

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        cliCommands.deleteBook(1L);
        Mockito.verify(bookRepo, Mockito.times(1)).delete(any(Book.class));
    }

    @DisplayName("List comments of specified book")
    @Test
    public void listBookComments() {

        long bookId = 1L;

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.getComments(bookCaptor.capture())).thenAnswer((Answer<List<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            int idx = (int) book.getId() - 1;
            return EXPECTED_COMMENTS.get(idx);
        });

        String result = cliCommands.listBookComments(bookId);
        assertEquals("""
                |----------|----------------------------------------------------------------------|
                |COMMENT ID|COMMENT TEXT                                                          |
                |----------|----------------------------------------------------------------------|
                |1         |Test book comment 1                                                   |
                |----------|----------------------------------------------------------------------|
                """, result);
    }

    @DisplayName("List specified comment of specified book")
    @Test
    public void listBookComment() {

        long bookId = 1L;

        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn("1");

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.getComment(bookCaptor.capture(), longCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            Long commentID = (Long) args[1];
            int bookIdx = (int) book.getId() - 1;
            int commentIdx = commentID.intValue() - 1;

            return Optional.ofNullable(EXPECTED_BOOKS.get(bookIdx).getBookComments().get(commentIdx));
        });

        String result = cliCommands.listBookComment(bookId);
        assertEquals("""
                |----------|----------------------------------------------------------------------|
                |COMMENT ID|COMMENT TEXT                                                          |
                |----------|----------------------------------------------------------------------|
                |1         |Test book comment 1                                                   |
                |----------|----------------------------------------------------------------------|
                """, result);
    }

    @DisplayName("Add comment to specified book")
    @Test
    public void addBookComment() {

        long bookId = 1L;
        String text = "Dummy comment";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(text);

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            int idx = ((Long) args[0]).intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        cliCommands.addBookComment(bookId);
        Mockito.verify(bookRepo, Mockito.times(1)).createComment(EXPECTED_BOOKS.get((int) bookId - 1), text);
    }

    @DisplayName("Update specified comment of specified book")
    @Test
    public void updateBookComment() {

        long bookId = 1L;
        long commentId = 1L;
        String text = "Dummy comment";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn(String.valueOf(commentId))
                .thenReturn(text);

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            int idx = ((Long) args[0]).intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.getComment(bookCaptor.capture(), longCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            Long commentID = (Long) args[1];
            int commentIdx = commentID.intValue() - 1;
            List<BookComment> bookComments = book.getBookComments();
            return Optional.ofNullable(bookComments.get(commentIdx));
        });

        Book expectedBook = EXPECTED_BOOKS.get((int) bookId - 1);
        BookComment expectedBookComment = expectedBook.getBookComments().get((int) commentId - 1);

        cliCommands.updateBookComment(bookId);
        Mockito.verify(bookRepo, Mockito.times(1)).updateComment(expectedBookComment, text);
    }

    @DisplayName("Delete specified comment from specified book")
    @Test
    public void deleteBookComment() {

        long bookId = 1L;
        long commentId = 1L;

        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(String.valueOf(commentId));

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            int idx = ((Long) args[0]).intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.getComment(bookCaptor.capture(), longCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            Long commentID = (Long) args[1];
            int commentIdx = commentID.intValue() - 1;
            List<BookComment> bookComments = book.getBookComments();
            return Optional.ofNullable(bookComments.get(commentIdx));
        });

        Book expectedBook = EXPECTED_BOOKS.get((int) bookId - 1);
        BookComment expectedBookComment = expectedBook.getBookComments().get((int) commentId - 1);

        cliCommands.deleteBookComment(bookId);
        Mockito.verify(bookRepo, Mockito.times(1)).deleteComment(expectedBookComment);
    }

    @DisplayName("Delete all comments of specified book")
    @Test
    public void deleteAllBookComments() {

        long bookId = 1L;

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            int idx = ((Long) args[0]).intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        Book expectedBook = EXPECTED_BOOKS.get((int) bookId - 1);

        cliCommands.deleteAllBookComments(bookId);
        Mockito.verify(bookRepo, Mockito.times(1)).deleteComments(expectedBook);
    }
}
