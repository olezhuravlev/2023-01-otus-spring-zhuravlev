package ru.otus.spring.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.model.Author;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.SysInfoService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorRestController.class)
public class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiGate apiGate;

    @MockBean
    private ModelAndViewPopulator populator;

    @MockBean
    private SysInfoService sysInfoService;

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();

    @BeforeAll
    public static void before() {
        EXPECTED_AUTHORS.add(new Author("a1", "Test author 1"));
        EXPECTED_AUTHORS.add(new Author("a2", "Test author 2"));
        EXPECTED_AUTHORS.add(new Author("a3", "Test author 3"));
    }

    @Test
    void testPostAuthors() throws Exception {

        given(apiGate.getAuthors()).willReturn(EXPECTED_AUTHORS);

        String expectedJson = new ObjectMapper().writeValueAsString(EXPECTED_AUTHORS);
        this.mockMvc.perform(post("/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
