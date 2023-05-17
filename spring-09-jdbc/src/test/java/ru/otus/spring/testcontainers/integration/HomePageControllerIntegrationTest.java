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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for home page")
@AutoConfigureMockMvc
class HomePageControllerIntegrationTest extends AbstractBaseContainer {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Request Home page")
    @Test
    void find() throws Exception {

        MvcResult mvcResult1 = this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();
        String mvcResultString1 = mvcResult1.getResponse().getContentAsString();

        MvcResult mvcResult2 = this.mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk()).andReturn();
        String mvcResultString2 = mvcResult2.getResponse().getContentAsString();

        MvcResult mvcResult3 = this.mockMvc.perform(MockMvcRequestBuilders.get("/books/"))
                .andExpect(status().isOk()).andReturn();
        String mvcResultString3 = mvcResult3.getResponse().getContentAsString();

        Assertions.assertThat(mvcResultString1).isEqualTo(mvcResultString2).isEqualTo(mvcResultString3);
    }
}
