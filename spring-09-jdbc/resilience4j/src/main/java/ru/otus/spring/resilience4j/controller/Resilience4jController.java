package ru.otus.spring.resilience4j.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.otus.spring.resilience4j.component.ExternalApiCaller;

@Controller
public class Resilience4jController {

    private final ExternalApiCaller apiCaller;

    public Resilience4jController(ExternalApiCaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    // @CircuitBreaker(name = "homeApi")
    // @Bulkhead(name = "homeApi")
    @GetMapping(value = {"/"})
    @ResponseBody
    public String home() {
        return apiCaller.callHomeApi();
    }
}
