package ru.otus.spring.resilience4j.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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

    @CircuitBreaker(name = "homeApi")
    @RateLimiter(name = "homeApi")
    @Bulkhead(name = "homeApi")
    @Retry(name = "homeApi")
    @TimeLimiter(name = "homeApi")
    @GetMapping(value = {"/"})
    @ResponseBody
    public String home() {
        return apiCaller.callHomeApi();
    }
}
