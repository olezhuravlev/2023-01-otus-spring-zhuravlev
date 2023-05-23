package ru.otus.spring.testcontainers.integration;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.testcontainers.AbstractBaseContainer;
import ru.otus.spring.testcontainers.WithMockAdmin;
import ru.otus.spring.testcontainers.WithMockAnonymous;
import ru.otus.spring.testcontainers.WithMockNonAdmin;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for home page")
@AutoConfigureMockMvc
class HomePageControllerIntegrationTest extends AbstractBaseContainer {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Request Home page")
    @Test
    @WithMockAdmin
    void requestHomePage() throws Exception {

        MvcResult mvcResult1 = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();
        String mvcResultString1 = mvcResult1.getResponse().getContentAsString();

        MvcResult mvcResult2 = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk()).andReturn();
        String mvcResultString2 = mvcResult2.getResponse().getContentAsString();

        MvcResult mvcResult3 = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books/"))
                .andExpect(status().isOk()).andReturn();
        String mvcResultString3 = mvcResult3.getResponse().getContentAsString();

        Assertions.assertThat(mvcResultString1).isEqualTo(mvcResultString2).isEqualTo(mvcResultString3);
    }

    @DisplayName("Request Home page by not authenticated user")
    @Test
    void requestHomePage_NotAuthenticated() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().is3xxRedirection());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books/"))
                .andExpect(status().is3xxRedirection());
    }

    @DisplayName("Request Home page by Anonymous user")
    @Test
    @WithMockAnonymous
    void requestHomePage_Anonymous() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books/"))
                .andExpect(status().isOk());
    }

    @DisplayName("Request Home page by authenticated non-Admin user")
    @Test
    @WithMockNonAdmin
    void requestHomePage_nonAdmin() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/books/"))
                .andExpect(status().isOk());
    }
}
