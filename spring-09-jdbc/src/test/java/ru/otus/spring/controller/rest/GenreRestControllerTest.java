package ru.otus.spring.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.SysInfoService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreRestController.class)
public class GenreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiGate apiGate;

    @MockBean
    private ModelAndViewPopulator populator;

    @MockBean
    private SysInfoService sysInfoService;

    private static final List<Genre> EXPECTED_GENRES = new ArrayList<>();

    @BeforeAll
    public static void before() {
        EXPECTED_GENRES.add(new Genre("g1", "Test genre 1"));
        EXPECTED_GENRES.add(new Genre("g2", "Test genre 2"));
        EXPECTED_GENRES.add(new Genre("g3", "Test genre 3"));
    }

    @Test
    void testPostGenres() throws Exception {

        given(apiGate.getGenres()).willReturn(EXPECTED_GENRES);

        String expectedJson = new ObjectMapper().writeValueAsString(EXPECTED_GENRES);
        this.mockMvc.perform(post("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
