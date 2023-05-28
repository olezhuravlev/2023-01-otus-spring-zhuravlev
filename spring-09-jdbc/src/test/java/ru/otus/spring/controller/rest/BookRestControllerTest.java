package ru.otus.spring.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.config.SecurityConfig;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.SysInfoService;
import ru.otus.spring.testcontainers.WithMockAdmin;

import javax.sql.DataSource;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
@ContextConfiguration(classes = {SecurityConfig.class, BookRestController.class})
public class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiGate apiGate;

    @MockBean
    private ModelAndViewPopulator populator;

    @MockBean
    private SysInfoService sysInfoService;

    @MockBean
    private DataSource dataSource;

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();
    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();
    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();
    private static final List<Book> EXPECTED_BOOKS = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {

        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));

        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));

        EXPECTED_COMMENTS.add(new BookComment(1, "Test book comment 1", 1));
        EXPECTED_COMMENTS.add(new BookComment(2, "Test book comment 2", 2));
        EXPECTED_COMMENTS.add(new BookComment(3, "Test book comment 3", 3));

        EXPECTED_BOOKS.add(new Book(1, "Test book 1", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0), Collections.singletonList(EXPECTED_COMMENTS.get(0))));
        EXPECTED_BOOKS.add(new Book(2, "Test book 2", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1), Collections.singletonList(EXPECTED_COMMENTS.get(1))));
        EXPECTED_BOOKS.add(new Book(3, "Test book 3", EXPECTED_AUTHORS.get(2), EXPECTED_GENRES.get(2), Collections.singletonList(EXPECTED_COMMENTS.get(2))));
    }

    @Test
    @WithMockAdmin
    void saveBook() throws Exception {

        long bookId = 1;

        Book book = EXPECTED_BOOKS.stream().filter(bookItem -> bookId == bookItem.getId()).findFirst().orElse(null);
        BookDto bookDto = BookDto.toDto(book);

        given(apiGate.saveBook(bookDto)).willReturn(book);

        String expectedJson = new ObjectMapper().writeValueAsString(bookDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/books")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(expectedJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithMockAdmin
    void deleteBookById() throws Exception {

        long bookId = 1;

        doNothing().when(apiGate).deleteBookById(bookId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", bookId);
        result.put("result", "ok");
        String expectedJson = new ObjectMapper().writeValueAsString(result);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
