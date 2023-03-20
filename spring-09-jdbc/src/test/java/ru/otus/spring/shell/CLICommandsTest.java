package ru.otus.spring.shell;

import org.junit.jupiter.api.BeforeAll;
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
//import ru.otus.spring.repositories.BookCommentRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;
import ru.otus.spring.service.printers.AuthorPrinter;
import ru.otus.spring.service.printers.BookCommentPrinter;
import ru.otus.spring.service.printers.BookPrinter;
import ru.otus.spring.service.printers.GenrePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@DisplayName("CLI test")
@DataJpaTest
@Import(value = {AppConfig.class, CLICommands.class, AuthorPrinter.class, GenrePrinter.class, BookPrinter.class, BookCommentPrinter.class, PrintProps.class})
public class CLICommandsTest {

    @MockBean
    private AuthorRepo authorRepo;

    @MockBean
    private GenreRepo genreRepo;

    @MockBean
    private BookRepo bookRepo;

//    @MockBean
//    private BookCommentRepo bookCommentRepo;

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

    @BeforeAll
    public static void before() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));

        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));

        EXPECTED_COMMENTS.add(List.of(new BookComment(1, "Test book comment 1")));
        EXPECTED_COMMENTS.add(List.of(new BookComment(2, "Test book comment 2")));
        EXPECTED_COMMENTS.add(List.of(new BookComment(3, "Test book comment 3")));

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), EXPECTED_COMMENTS.get(0)));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), EXPECTED_COMMENTS.get(1)));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), EXPECTED_COMMENTS.get(2)));
    }

    @DisplayName("Get list of all authors")
    @Test
    public void getAuthors() {
        Mockito.when(authorRepo.read()).thenReturn(EXPECTED_AUTHORS);
        String result = cliCommands.getAuthors();
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
    public void getGenres() {
        Mockito.when(genreRepo.read()).thenReturn(EXPECTED_GENRES);
        String result = cliCommands.getGenres();
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
    public void getBooks() {
        Mockito.when(bookRepo.findAll()).thenReturn(EXPECTED_BOOKS);
        String result = cliCommands.getBook();
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
    public void getBookById() {

        long bookId = 1L;

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
        });

        String result = cliCommands.getBook(bookId);
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

        String bookTitle = "book";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.find(stringCaptor.capture())).thenAnswer((Answer<List<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            return EXPECTED_BOOKS.stream().filter(book -> book.getTitle().contains(arg)).toList();
        });

        String result = cliCommands.getBook(bookTitle);
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

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.when(authorRepo.read(longCaptor.capture())).thenAnswer((Answer<Optional<Author>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_AUTHORS.get(idx));
        });

        Mockito.when(genreRepo.read(longCaptor.capture())).thenAnswer((Answer<Optional<Genre>>) invocation -> {
            Object[] args = invocation.getArguments();
            Long arg = (Long) args[0];
            int idx = arg.intValue() - 1;
            return Optional.ofNullable(EXPECTED_GENRES.get(idx));
        });

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        ArgumentCaptor<Genre> genreCaptor = ArgumentCaptor.forClass(Genre.class);
        Mockito.when(bookRepo.create(stringCaptor.capture(), authorCaptor.capture(), genreCaptor.capture())).thenAnswer((Answer<Book>) invocation -> {
            Object[] args = invocation.getArguments();
            String title = (String) args[0];
            Author author = (Author) args[1];
            Genre genre = (Genre) args[2];
            return new Book(0L, title, author, genre, new ArrayList<>());
        });

        cliCommands.addBook();
        Mockito.verify(bookRepo, Mockito.times(1)).create("Dummy book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(1));
    }

    // public String updateBook(long id)
//    @DisplayName("Update book by ID")
//    @Test
//    public void updateBookById() {
//
//        Mockito.when(bookRepo.update(anyLong(), any(String.class), any(Author.class), any(Genre.class))).thenReturn(1);
//        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
//                .thenReturn("Dummy book")
//                .thenReturn("1")
//                .thenReturn("2");
//
//        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.when(authorRepo.read(longCaptor.capture())).thenAnswer((Answer<Optional<Author>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Long arg = (Long) args[0];
//            int idx = arg.intValue() - 1;
//            return Optional.ofNullable(EXPECTED_AUTHORS.get(idx));
//        });
//
//        Mockito.when(genreRepo.read(longCaptor.capture())).thenAnswer((Answer<Optional<Genre>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Long arg = (Long) args[0];
//            int idx = arg.intValue() - 1;
//            return Optional.ofNullable(EXPECTED_GENRES.get(idx));
//        });
//
//        Mockito.when(bookRepo.read(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Long arg = (Long) args[0];
//            int idx = arg.intValue() - 1;
//            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
//        });
//
//        cliCommands.updateBook(1L);
//        Mockito.verify(bookRepo, Mockito.times(1)).update(1L, "Dummy book", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(1));
//    }

//    @DisplayName("Delete book by ID")
//    @Test
//    public void deleteBookById() {
//        cliCommands.deleteBook(1L);
//        Mockito.verify(bookRepo, Mockito.times(1)).remove(any(Book.class));
//    }
//
//    @DisplayName("List comments of specified book")
//    @Test
//    public void listBookComments() {
//
//        long bookId = 1L;
//
//        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Long arg = (Long) args[0];
//            int idx = arg.intValue() - 1;
//            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
//        });
//
//        Mockito.when(bookCommentRepo.find(longCaptor.capture())).thenAnswer((Answer<List<BookComment>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Book argBook = (Book) args[0];
//            int idx = (int) argBook.getId() - 1;
//            return EXPECTED_COMMENTS.get(idx);
//        });
//
//        String result = cliCommands.listBookComments(bookId);
//        assertEquals("""
//                |----------|----------------------------------------------------------------------|
//                |COMMENT ID|COMMENT TEXT                                                          |
//                |----------|----------------------------------------------------------------------|
//                |1         |Test book comment 1                                                   |
//                |----------|----------------------------------------------------------------------|
//                """, result);
//    }
//
//    @DisplayName("Add comment to specified book")
//    @Test
//    public void addBookComment() {
//
//        long bookId = 1L;
//        String text = "Dummy comment";
//
//        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(text);
//
//        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            int idx = ((Long) args[0]).intValue() - 1;
//            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
//        });
//
//        cliCommands.addBookComment(bookId);
//        Mockito.verify(bookCommentRepo, Mockito.times(1)).create(EXPECTED_BOOKS.get((int) bookId - 1), text);
//    }
//
//    @DisplayName("Update specified comment of specified book")
//    @Test
//    public void updateBookComment() {
//
//        long bookId = 1L;
//        long commentId = 1L;
//        String text = "Dummy comment";
//
//        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
//                .thenReturn(String.valueOf(commentId))
//                .thenReturn(text);
//
//        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            int idx = ((Long) args[0]).intValue() - 1;
//            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
//        });
//
//        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
//        Mockito.when(bookCommentRepo.find(bookCaptor.capture(), longCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Book argBook = (Book) args[0];
//            Long argCommentId = (Long) args[1];
//            int idx = argCommentId.intValue() - 1;
//            List<BookComment> bookComments = argBook.getBookComments();
//            return Optional.ofNullable(bookComments.get(idx));
//        });
//
//        Book expectedBook = EXPECTED_BOOKS.get((int) bookId - 1);
//        List<BookComment> expectedBookComments = expectedBook.getBookComments();
//
//        cliCommands.updateBookComment(bookId);
//        Mockito.verify(bookCommentRepo, Mockito.times(1)).update(expectedBook, expectedBookComments.get((int) commentId - 1), text);
//    }
//
//    @DisplayName("Delete specified comment from specified book")
//    @Test
//    public void deleteBookComment() {
//
//        long bookId = 1L;
//        long commentId = 1L;
//
//        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(String.valueOf(commentId));
//
//        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            int idx = ((Long) args[0]).intValue() - 1;
//            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
//        });
//
//        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
//        Mockito.when(bookCommentRepo.find(bookCaptor.capture(), longCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            Book argBook = (Book) args[0];
//            Long argCommentId = (Long) args[1];
//            int idx = argCommentId.intValue() - 1;
//            List<BookComment> bookComments = argBook.getBookComments();
//            return Optional.ofNullable(bookComments.get(idx));
//        });
//
//        Book expectedBook = EXPECTED_BOOKS.get((int) bookId - 1);
//        List<BookComment> expectedBookComments = expectedBook.getBookComments();
//
//        cliCommands.deleteBookComment(bookId);
//        Mockito.verify(bookCommentRepo, Mockito.times(1)).remove(expectedBook, expectedBookComments.get((int) commentId - 1));
//    }
//
//    @DisplayName("Delete all comments of specified book")
//    @Test
//    public void deleteAllBookComments() {
//
//        long bookId = 1L;
//
//        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.when(bookRepo.find(longCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
//            Object[] args = invocation.getArguments();
//            int idx = ((Long) args[0]).intValue() - 1;
//            return Optional.ofNullable(EXPECTED_BOOKS.get(idx));
//        });
//
//        Book expectedBook = EXPECTED_BOOKS.get((int) bookId - 1);
//
//        cliCommands.deleteAllBookComments(bookId);
//        Mockito.verify(bookCommentRepo, Mockito.times(1)).removeAll(expectedBook);
//    }
}
