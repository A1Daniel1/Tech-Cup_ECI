package edu.escuelaing.techcup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * TechCup Futbol backend: a modular monolith. Each functional domain of the
 * specification (identity, players, teams, tournaments, competition) is a package
 * with four layers (api / application / domain / infrastructure); {@code shared}
 * holds the cross-cutting concerns that the specification calls "orchestrator".
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TechcupApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechcupApplication.class, args);
    }
}
