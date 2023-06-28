package ru.otus.spring.resilience4j.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resilience4jController {

    @GetMapping("/fallback")
    public String fallback() {
        return "Sorry, we're out of order!";
    }
}
