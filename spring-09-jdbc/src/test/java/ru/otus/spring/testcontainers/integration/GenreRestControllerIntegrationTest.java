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
import ru.otus.spring.model.Genre;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Genres")
@AutoConfigureMockMvc
public class GenreRestControllerIntegrationTest extends AbstractBaseContainer {

    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));
    }

    @DisplayName("Request all genres from DB")
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void requestAllGenres() throws Exception {
        String expectedJson = new ObjectMapper().writeValueAsString(EXPECTED_GENRES);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("Request all genres by not authenticated user")
    @Test
    void requestAllGenres_NotAuthenticated() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/genres"))
                .andExpect(status().is3xxRedirection());
    }

    @DisplayName("Request all genres by Anonymous user")
    @Test
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void requestAllGenres_Anonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/genres"))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("Request all genres by authenticated non-Admin user")
    @Test
    @WithMockUser(authorities = {"ROLE_COMMENTER", "ROLE_READER"})
    void requestAllGenres_nonAdmin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/genres"))
                .andExpect(status().is4xxClientError());
    }
}
