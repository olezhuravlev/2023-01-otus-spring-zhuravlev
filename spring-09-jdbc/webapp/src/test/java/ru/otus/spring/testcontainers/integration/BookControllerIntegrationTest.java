package ru.otus.spring.testcontainers.integration;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.testcontainers.AbstractBaseContainer;
import ru.otus.spring.testcontainers.WithMockAdmin;
import ru.otus.spring.testcontainers.WithMockAnonymous;
import ru.otus.spring.testcontainers.WithMockNonAdmin;

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
    @WithMockAdmin
    void requestBookPage() throws Exception {

        long bookId = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html")))
                .andExpect(content().string(containsString("/html>")));

        bookId = 2;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html")))
                .andExpect(content().string(containsString("/html>")));

        bookId = 3;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html")))
                .andExpect(content().string(containsString("/html>")));
    }

    @DisplayName("Request Book page by not authenticated user")
    @Test
    void requestBookPage_NotAuthenticated() throws Exception {

        long bookId = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is3xxRedirection());

        bookId = 2;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is3xxRedirection());

        bookId = 3;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is3xxRedirection());
    }

    @DisplayName("Request Book page by Anonymous user")
    @Test
    @WithMockAnonymous
    void requestBookPage_Anonymous() throws Exception {

        long bookId = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is4xxClientError());

        bookId = 2;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is4xxClientError());

        bookId = 3;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is4xxClientError());
    }

    @DisplayName("Request Book page by non-Admin user")
    @Test
    @WithMockNonAdmin
    void requestBookPage_nonAdmin() throws Exception {

        long bookId = 1;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().is4xxClientError());

        bookId = 2;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().isOk());

        bookId = 3;
        this.mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", bookId)).andExpect(status().isOk());
    }
}
