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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for error page")
@AutoConfigureMockMvc
class AppErrorControllerIntegrationTest extends AbstractBaseContainer {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Request error page")
    @Test
    @WithMockAdmin
    void requestErrorPage() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html")))
                .andExpect(content().string(containsString("/html>")));
    }

    @DisplayName("Request error page by not authenticated user")
    @Test
    void requestErrorPage_NotAuthenticated() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/error")).andExpect(status().is3xxRedirection());
    }

    @DisplayName("Request error page by Anonymous user")
    @Test
    @WithMockAnonymous
    void requestErrorPage_Anonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/error")).andExpect(status().isOk());
    }
}
