package edu.escuelaing.techcup.shared.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JSON conventions: ISO-8601 dates and explicit nulls.
 *
 * <p>Nullable properties are serialized as {@code null} rather than omitted so that every
 * response matches the documented DTO shape. Clients can then rely on the key always being
 * present, instead of having to distinguish "absent" from "null" for optional fields such as
 * a match venue, referee or score.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer techcupJacksonCustomizer() {
        return builder -> builder
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializationInclusion(JsonInclude.Include.ALWAYS);
    }
}
