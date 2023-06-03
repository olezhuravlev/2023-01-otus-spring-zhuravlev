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
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.SysInfoService;
import ru.otus.spring.testcontainers.WithMockAdmin;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreRestController.class)
@ContextConfiguration(classes = {SecurityConfig.class, GenreRestController.class})
public class GenreRestControllerTest {

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

    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_GENRES.add(new Genre(1, "Test genre 1"));
        EXPECTED_GENRES.add(new Genre(2, "Test genre 2"));
        EXPECTED_GENRES.add(new Genre(3, "Test genre 3"));
    }

    @Test
    @WithMockAdmin
    void getGenres() throws Exception {

        given(apiGate.getGenres()).willReturn(EXPECTED_GENRES);

        String expectedJson = new ObjectMapper().writeValueAsString(EXPECTED_GENRES);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
