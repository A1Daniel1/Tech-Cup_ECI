package edu.escuelaing.techcup.shared.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes the system clock as a bean. Every rule that compares against "today" or "now"
 * (tournament start and finish dates, registration deadline, match kick-off) reads it through
 * this seam, which makes those rules unit-testable with a fixed clock.
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
