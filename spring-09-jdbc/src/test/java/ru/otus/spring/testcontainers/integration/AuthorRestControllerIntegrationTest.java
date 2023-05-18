package ru.otus.spring.testcontainers.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.model.Author;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Authors")
@AutoConfigureMockMvc
public class AuthorRestControllerIntegrationTest extends AbstractBaseContainer {

    private static final List<Author> EXPECTED_AUTHORS = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_AUTHORS.add(new Author(1, "Test author 1"));
        EXPECTED_AUTHORS.add(new Author(2, "Test author 2"));
        EXPECTED_AUTHORS.add(new Author(3, "Test author 3"));
    }

    @DisplayName("Request all authors from DB")
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void requestAllAuthors() throws Exception {
        String expectedJson = new ObjectMapper().writeValueAsString(EXPECTED_AUTHORS);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Request all authors by not authenticated user")
    @Test
    void requestAllAuthors_NotAuthenticated() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/authors")).andExpect(status().is3xxRedirection());
    }

    @DisplayName("Request all authors by Anonymous user")
    @Test
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void requestAllAuthors_Anonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/authors")).andExpect(status().is4xxClientError());
    }

    @DisplayName("Request all authors by authenticated non-Admin user")
    @Test
    @WithMockUser(authorities = {"ROLE_COMMENTER", "ROLE_READER"})
    void requestAllAuthors_nonAdmin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/authors")).andExpect(status().is4xxClientError());
    }
}
