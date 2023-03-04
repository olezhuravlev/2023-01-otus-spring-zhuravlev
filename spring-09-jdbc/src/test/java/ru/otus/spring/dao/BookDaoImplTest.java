package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.spring.model.Book;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DAO for Books")
@SpringBootTest
@Import(BookDaoImpl.class)
public class BookDaoImplTest {

    @Autowired
    private BookDao bookDao;

    @DisplayName("Retrieve all books from DB")
    @Test
    public void read() {
        List<Book> expected = new ArrayList<>();
        expected.add(new Book(1, "Test book 1", "Test author 1", "Test genre 1"));
        expected.add(new Book(2, "Test book 2", "Test author 2", "Test genre 2"));
        expected.add(new Book(3, "Test book 3", "Test author 3", "Test genre 3"));

        List<Book> books = bookDao.read();
        assertThat(books).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("Retrieve book by ID")
    @Test
    public void readById() {
        Book expected = new Book(1, "Test book 1", "Test author 1", "Test genre 1");
        Book books = bookDao.read(1);
        assertThat(books).isEqualTo(expected);
    }

    @DisplayName("Retrieve books by title")
    @Test
    public void readByTitle() {
        List<Book> expected = new ArrayList<>();
        expected.add(new Book(1, "Test book 1", "Test author 1", "Test genre 1"));
        List<Book> books = bookDao.read("book 1");
        assertThat(books).containsExactlyInAnyOrderElementsOf(expected);

        expected.add(new Book(2, "Test book 2", "Test author 2", "Test genre 2"));
        expected.add(new Book(3, "Test book 3", "Test author 3", "Test genre 3"));
        books = bookDao.read("book");
        assertThat(books).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("Create a new book with autoincremented ID")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void create() {
        List<Book> expected = new ArrayList<>();
        expected.add(new Book(1, "Test book 1", "Test author 1", "Test genre 1"));
        expected.add(new Book(2, "Test book 2", "Test author 2", "Test genre 2"));
        expected.add(new Book(3, "Test book 3", "Test author 3", "Test genre 3"));
        expected.add(new Book(100, "Test book 4", "Test author 1", "Test genre 1"));

        int result = bookDao.create("Test book 4", 1, 1);
        assertThat(result).isEqualTo(1);

        List<Book> books = bookDao.read();
        assertThat(books).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("Update existing book")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void update() {
        List<Book> expected = new ArrayList<>();
        expected.add(new Book(1, "Test book 1_1", "Test author 2", "Test genre 3"));
        expected.add(new Book(2, "Test book 2", "Test author 2", "Test genre 2"));
        expected.add(new Book(3, "Test book 3", "Test author 3", "Test genre 3"));

        int result = bookDao.update(1, "Test book 1_1", 2, 3);
        assertThat(result).isEqualTo(1);

        List<Book> books = bookDao.read();
        assertThat(books).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("Delete existing book")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void delete() {
        List<Book> expected = new ArrayList<>();
        expected.add(new Book(2, "Test book 2", "Test author 2", "Test genre 2"));
        expected.add(new Book(3, "Test book 3", "Test author 3", "Test genre 3"));

        int result = bookDao.delete(1);
        assertThat(result).isEqualTo(1);

        List<Book> books = bookDao.read();
        assertThat(books).containsExactlyInAnyOrderElementsOf(expected);
    }
}
