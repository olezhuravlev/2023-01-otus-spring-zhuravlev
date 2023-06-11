package ru.otus.spring.healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WebappSystemTimeHealthIndicator implements HealthIndicator {

    private static final String METRIC_NAME = "webapp_system_time";

    @Override
    public Health health() {
        Date date = new Date();
        return Health.up().withDetail(METRIC_NAME, date).build();
    }
}
