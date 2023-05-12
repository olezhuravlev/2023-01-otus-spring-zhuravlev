package ru.otus.spring.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.service.SysInfoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AppErrorController.class)
class AppErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelAndViewPopulator populator;

    @MockBean
    private SysInfoService sysInfoService;

    @Test
    void testError() throws Exception {

        String expectedViewName = "error/404";

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(expectedViewName);

        given(populator.fillError404(any(HttpServletRequest.class), any(ModelAndView.class))).willReturn(modelAndView);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedViewName));
    }
}
