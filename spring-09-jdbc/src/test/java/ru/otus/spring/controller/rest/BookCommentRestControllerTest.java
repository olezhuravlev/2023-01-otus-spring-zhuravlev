package ru.otus.spring.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.service.ApiGate;
import ru.otus.spring.service.SysInfoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCommentRestController.class)
public class BookCommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiGate apiGate;

    @MockBean
    private ModelAndViewPopulator populator;

    @MockBean
    private SysInfoService sysInfoService;

    private static final List<BookComment> EXPECTED_COMMENTS = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {
        EXPECTED_COMMENTS.add(new BookComment(1, "Test book comment 1", 1));
        EXPECTED_COMMENTS.add(new BookComment(2, "Test book comment 2", 2));
        EXPECTED_COMMENTS.add(new BookComment(3, "Test book comment 3", 3));
    }

    @Test
    void testPutComment() throws Exception {

        long commentId = 1;

        BookComment bookComment = EXPECTED_COMMENTS.stream().filter(comment -> commentId == comment.getId()).findFirst().orElse(null);
        BookCommentDto bookCommentDto = BookCommentDto.toDto(bookComment);

        given(apiGate.saveBookComment(bookCommentDto)).willReturn(bookComment);

        String expectedJson = new ObjectMapper().writeValueAsString(bookCommentDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/comments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(expectedJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    void testDeleteComments() throws Exception {

        long commentId = 1;

        doNothing().when(apiGate).deleteBookCommentById(commentId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", commentId);
        result.put("result", "ok");
        String expectedJson = new ObjectMapper().writeValueAsString(result);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
