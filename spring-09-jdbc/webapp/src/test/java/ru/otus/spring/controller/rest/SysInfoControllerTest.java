package ru.otus.spring.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.component.ModelAndViewPopulator;
import ru.otus.spring.component.SysInfoArgumentResolver;
import ru.otus.spring.config.ApplicationConfig;
import ru.otus.spring.config.SecurityConfig;
import ru.otus.spring.dto.SysInfoDto;
import ru.otus.spring.service.SysInfoService;
import ru.otus.spring.testcontainers.WithMockAdmin;

import javax.sql.DataSource;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SysInfoController.class)
@ContextConfiguration(classes = {ApplicationConfig.class, SecurityConfig.class, SysInfoController.class, SysInfoArgumentResolver.class})
public class SysInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelAndViewPopulator populator;

    @MockBean
    private SysInfoService sysInfoService;

    @MockBean
    private DataSource dataSource;

    @Test
    @WithMockAdmin
    void getSysInfo() throws Exception {

        SysInfoDto expected = new SysInfoDto("1", "2", "3", "4");
        given(sysInfoService.getSysInfo()).willReturn(expected);

        String expectedJson = new ObjectMapper().writeValueAsString(expected);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/sysinfo"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
