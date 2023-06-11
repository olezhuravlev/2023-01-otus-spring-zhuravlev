package ru.otus.spring.healthcheck;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Endpoint(id = "custom-health")
@Component
public class WebappSystemTimeCustomEndpoint {

    private final WebappSystemTimeHealthIndicator webappSystemTimeHealthIndicator;

    public WebappSystemTimeCustomEndpoint(WebappSystemTimeHealthIndicator webappSystemTimeHealthIndicator) {
        this.webappSystemTimeHealthIndicator = webappSystemTimeHealthIndicator;
    }

    @ReadOperation
    @Bean
    public String health() {

        Health health = webappSystemTimeHealthIndicator.getHealth(true);
        Map<String, Object> details = health.getDetails();

        return details.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining());
    }
}
