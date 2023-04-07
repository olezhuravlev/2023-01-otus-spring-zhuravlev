package ru.otus.spring.shell;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.spring.configs.AppConfig;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repositories.AuthorRepo;
import ru.otus.spring.repositories.BookCommentRepo;
import ru.otus.spring.repositories.BookRepo;
import ru.otus.spring.repositories.GenreRepo;
import ru.otus.spring.service.ApiGateImpl;
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
@DataMongoTest
@ContextConfiguration(classes = {AppConfig.class, CLICommands.class, ApiGateImpl.class, CLIValueProvider.class, AuthorPrinter.class, GenrePrinter.class, BookPrinter.class, BookCommentPrinter.class})
public class CLICommandsTest {

    @MockBean
    private AuthorRepo authorRepo;

    @MockBean
    private GenreRepo genreRepo;

    @MockBean
    private BookRepo bookRepo;

    @MockBean
    private BookCommentRepo bookCommentRepo;

    @MockBean
    private CLIValueProvider cliValueProvider;

    @Autowired
    @InjectMocks
    private CLICommands cliCommands;

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();
    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {

        EXPECTED_AUTHORS.clear();
        EXPECTED_GENRES.clear();
        EXPECTED_COMMENTS.clear();
        EXPECTED_BOOKS.clear();

        EXPECTED_AUTHORS.add(new Author("a1", "Test author 1"));
        EXPECTED_AUTHORS.add(new Author("a2", "Test author 2"));
        EXPECTED_AUTHORS.add(new Author("a3", "Test author 3"));

        EXPECTED_GENRES.add(new Genre("g1", "Test genre 1"));
        EXPECTED_GENRES.add(new Genre("g2", "Test genre 2"));
        EXPECTED_GENRES.add(new Genre("g3", "Test genre 3"));

        EXPECTED_COMMENTS.add(new BookComment(new ObjectId("bc1111111111111111111111"), "Test book comment 1", "b1"));
        EXPECTED_COMMENTS.add(new BookComment(new ObjectId("bc2222222222222222222222"), "Test book comment 2", "b2"));
        EXPECTED_COMMENTS.add(new BookComment(new ObjectId("bc3333333333333333333333"), "Test book comment 3", "b3"));

        // Comments must be modifiable!
        List<BookComment> comments1 = new ArrayList<>();
        comments1.add(EXPECTED_COMMENTS.get(0));
        EXPECTED_BOOKS.add(new Book("b1", "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), comments1));

        List<BookComment> comments2 = new ArrayList<>();
        comments2.add(EXPECTED_COMMENTS.get(1));
        EXPECTED_BOOKS.add(new Book("b2", "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), comments2));

        List<BookComment> comments3 = new ArrayList<>();
        comments3.add(EXPECTED_COMMENTS.get(2));
        EXPECTED_BOOKS.add(new Book("b3", "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), comments3));
    }

    @DisplayName("Get list of all authors")
    @Test
    public void getAuthorsList() {

        Mockito.when(authorRepo.findAll()).thenReturn(EXPECTED_AUTHORS);

        String result = cliCommands.getAuthorsList();
        assertEquals("""
                |------------------------|----------------------------------------|
                |AUTHOR ID               |NAME                                    |
                |------------------------|----------------------------------------|
                |a1                      |Test author 1                           |
                |a2                      |Test author 2                           |
                |a3                      |Test author 3                           |
                |------------------------|----------------------------------------|
                """, result);
    }

    @DisplayName("Get list of all genres")
    @Test
    public void getGenresList() {

        Mockito.when(genreRepo.findAll()).thenReturn(EXPECTED_GENRES);

        String result = cliCommands.getGenresList();
        assertEquals("""
                |------------------------|----------------------------------------|
                |GENRE ID                |NAME                                    |
                |------------------------|----------------------------------------|
                |g1                      |Test genre 1                            |
                |g2                      |Test genre 2                            |
                |g3                      |Test genre 3                            |
                |------------------------|----------------------------------------|
                """, result);
    }

    @DisplayName("Get list of all books")
    @Test
    public void getBooksList() {

        Mockito.when(bookRepo.findAll()).thenReturn(EXPECTED_BOOKS);

        String result = cliCommands.getBooksList();
        assertEquals("""
                |------------------------|----------------------------------------|------------------------------|---------------|
                |BOOK ID                 |TITLE                                   |AUTHOR                        |GENRE          |
                |------------------------|----------------------------------------|------------------------------|---------------|
                |b1                      |Test book 1                             |Test author 1                 |Test genre 1   |
                |b2                      |Test book 2                             |Test author 2                 |Test genre 2   |
                |b3                      |Test book 3                             |Test author 3                 |Test genre 3   |
                |------------------------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Get book by ID")
    @Test
    public void getBookById() {

        String bookId = "b1";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().equals(arg)).findFirst();
            return found;
        });

        String result = cliCommands.getBookById(bookId);
        assertEquals("""
                |------------------------|----------------------------------------|------------------------------|---------------|
                |BOOK ID                 |TITLE                                   |AUTHOR                        |GENRE          |
                |------------------------|----------------------------------------|------------------------------|---------------|
                |b1                      |Test book 1                             |Test author 1                 |Test genre 1   |
                |------------------------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Find book(s) by title")
    @Test
    public void findBooksByTitle() {

        String bookTitle = "book";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findByTitleContainingIgnoreCase(stringCaptor.capture())).thenAnswer((Answer<List<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            List<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getTitle().contains(arg)).toList();
            return found;
        });

        String result = cliCommands.findBooksByTitle(bookTitle);
        assertEquals("""
                |------------------------|----------------------------------------|------------------------------|---------------|
                |BOOK ID                 |TITLE                                   |AUTHOR                        |GENRE          |
                |------------------------|----------------------------------------|------------------------------|---------------|
                |b1                      |Test book 1                             |Test author 1                 |Test genre 1   |
                |b2                      |Test book 2                             |Test author 2                 |Test genre 2   |
                |b3                      |Test book 3                             |Test author 3                 |Test genre 3   |
                |------------------------|----------------------------------------|------------------------------|---------------|
                """, result);
    }

    @DisplayName("Add new book")
    @Test
    public void addBook() {

        String bookTitle = "Dummy book";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn(bookTitle)
                .thenReturn("a1")
                .thenReturn("g2");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.save(bookCaptor.capture())).thenAnswer((Answer<Book>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            return book;
        });

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(authorRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Author>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Author> found = EXPECTED_AUTHORS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        Mockito.when(genreRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Genre>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Genre> found = EXPECTED_GENRES.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        cliCommands.addBook();
        Mockito.verify(bookRepo, Mockito.times(1)).save(new Book(bookTitle, EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(1), new ArrayList<>()));
    }

    @DisplayName("Update book identified by ID")
    @Test
    public void updateBookById() {

        String bookId = "b1";
        String bookTitle = "Test book";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString()))
                .thenReturn(bookTitle)
                .thenReturn("a1")
                .thenReturn("g2");

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        Mockito.when(authorRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Author>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Author> found = EXPECTED_AUTHORS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        Mockito.when(genreRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Genre>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Genre> found = EXPECTED_GENRES.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.when(bookRepo.save(bookCaptor.capture())).thenAnswer((Answer<Book>) invocation -> {
            Object[] args = invocation.getArguments();
            Book book = (Book) args[0];
            return book;
        });

        cliCommands.updateBook(bookId);

        Book result = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(bookId)).findFirst().get();
        Mockito.verify(bookRepo, Mockito.times(1)).save(result);
    }

    @DisplayName("Delete book by ID")
    @Test
    public void deleteBookById() {

        String bookId = "b1";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        cliCommands.deleteBook(bookId);

        Book result = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(bookId)).findFirst().get();
        Mockito.verify(bookRepo, Mockito.times(1)).delete(result);
    }

    @DisplayName("List all comments of a specified book")
    @Test
    public void listBookComments() {

        String bookId = "b1";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        String result = cliCommands.listBookComments(bookId);
        assertEquals("""
                |------------------------|----------------------------------------------------------------------|
                |COMMENT ID              |COMMENT TEXT                                                          |
                |------------------------|----------------------------------------------------------------------|
                |bc1111111111111111111111|Test book comment 1                                                   |
                |------------------------|----------------------------------------------------------------------|
                """, result);
    }

    @DisplayName("Find book comment by ID")
    @Test
    public void findBookCommentById() {

        String bookCommentId = "bc1111111111111111111111";

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookCommentRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<BookComment> found = EXPECTED_COMMENTS.stream().filter(comment -> comment.getId().toString().equals(arg)).findFirst();
            return found;
        });

        String result = cliCommands.findBookCommentById(bookCommentId);
        assertEquals("""
                |------------------------|----------------------------------------------------------------------|
                |COMMENT ID              |COMMENT TEXT                                                          |
                |------------------------|----------------------------------------------------------------------|
                |bc1111111111111111111111|Test book comment 1                                                   |
                |------------------------|----------------------------------------------------------------------|
                """, result);
    }

    @DisplayName("Add a comment to specified book")
    @Test
    public void addBookComment() {

        String bookId = "b1";
        String text = "Test comment";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(text);
        Mockito.when(bookRepo.existsById(bookId)).thenAnswer((Answer<Boolean>) invocation -> true);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        cliCommands.addBookComment(bookId);

        Book resultBook = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(bookId)).findFirst().get();
        Mockito.verify(bookRepo, Mockito.times(1)).save(resultBook);
        Mockito.verify(bookCommentRepo, Mockito.times(1)).save(any(BookComment.class));
    }

    @DisplayName("Update specified book comment")
    @Test
    public void updateBookComment() {

        String bookId = "b1";
        String bookCommentId = "bc1111111111111111111111";
        String text = "Test comment";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(text);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookCommentRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<BookComment> found = EXPECTED_COMMENTS.stream().filter(comment -> comment.getId().toString().equals(arg)).findFirst();
            return found;
        });

        cliCommands.updateBookComment(bookCommentId);

        BookComment result = new BookComment(new ObjectId(bookCommentId), text, bookId);
        Mockito.verify(bookCommentRepo, Mockito.times(1)).save(result);
    }

    @DisplayName("Delete specified book comment")
    @Test
    public void deleteBookComment() {

        String bookId = "b1";
        String bookCommentId = "bc1111111111111111111111";

        Mockito.when(cliValueProvider.getValue(Mockito.anyString())).thenReturn(bookCommentId);
        Mockito.when(bookCommentRepo.existsById(bookCommentId)).thenAnswer((Answer<Boolean>) invocation -> true);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookCommentRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<BookComment>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<BookComment> found = EXPECTED_COMMENTS.stream().filter(comment -> comment.getId().toString().equals(arg)).findFirst();
            return found;
        });

        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        cliCommands.deleteBookComment(bookCommentId);

        Book result = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(bookId)).findFirst().get();
        Mockito.verify(bookRepo, Mockito.times(1)).save(result);
        Mockito.verify(bookCommentRepo, Mockito.times(1)).deleteById(bookCommentId);
    }

    @DisplayName("Delete all comments of specified book")
    @Test
    public void deleteAllBookComments() {

        String bookId = "b1";

        Mockito.when(bookRepo.existsById(bookId)).thenAnswer((Answer<Boolean>) invocation -> true);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(bookRepo.findById(stringCaptor.capture())).thenAnswer((Answer<Optional<Book>>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            Optional<Book> found = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(arg)).findFirst();
            return found;
        });

        cliCommands.deleteAllBookComments(bookId);

        Book result = EXPECTED_BOOKS.stream().filter(book -> book.getId().contains(bookId)).findFirst().get();
        result.setBookComments(new ArrayList<>());
        Mockito.verify(bookRepo, Mockito.times(1)).save(result);
    }
}
