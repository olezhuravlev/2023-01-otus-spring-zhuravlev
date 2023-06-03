package ru.otus.spring.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.config.SecurityConfig;
import ru.otus.spring.model.Author;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.SysInfoService;
import ru.otus.spring.testcontainers.WithMockAdmin;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorRestController.class)
@ContextConfiguration(classes = {SecurityConfig.class, AuthorRestController.class})
public class AuthorRestControllerTest {

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

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));
    }

    @Test
    @WithMockAdmin
    void getAuthors() throws Exception {

        given(apiGate.getAuthors()).willReturn(EXPECTED_AUTHORS);

        String expectedJson = new ObjectMapper().writeValueAsString(EXPECTED_AUTHORS);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/authors").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
