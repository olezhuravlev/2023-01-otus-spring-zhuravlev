package ru.otus.spring.resilience4j.component;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalApiCaller {

    private final RestTemplate restTemplate;

    public ExternalApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callHomeApi() {
        String result = restTemplate.getForObject("/", String.class);
        return result;
    }
}
