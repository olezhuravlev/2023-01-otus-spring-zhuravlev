package ru.otus.spring.testcontainers.integration;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.testcontainers.AbstractBaseContainer;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for book page")
@AutoConfigureMockMvc
class BookControllerIntegrationTest extends AbstractBaseContainer {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Request Book page")
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void requestBookPage() throws Exception {

        long bookId = 1;

        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html")))
                .andExpect(content().string(containsString("/html>")));
    }

    @DisplayName("Request Book page by not authenticated user")
    @Test
    void requestBookPage_NotAuthenticated() throws Exception {

        long bookId = 1;

        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().is3xxRedirection());
    }

    @DisplayName("Request Book page by Anonymous user")
    @Test
    @WithMockUser(authorities = {"ROLE_ANONYMOUS"})
    void requestBookPage_Anonymous() throws Exception {

        long bookId = 1;

        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().isOk());
    }

    @DisplayName("Request Book page by non-Admin user")
    @Test
    @WithMockUser(authorities = {"ROLE_COMMENTER", "ROLE_READER"})
    void requestBookPage_nonAdmin() throws Exception {

        long bookId = 1;

        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().isOk());
    }
}
