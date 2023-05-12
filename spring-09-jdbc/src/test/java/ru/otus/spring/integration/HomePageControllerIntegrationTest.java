package ru.otus.spring.integration;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for home page")
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class HomePageControllerIntegrationTest {

    private static final String DATABASE_NAME = "librarydb_test";

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

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
