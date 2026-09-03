package edu.escuelaing.techcup.identity.domain;

/** Academic programs; the four engineering programs organize the tournament, {@code OTHER} is anything else. */
public enum AcademicProgram {
    SYSTEMS_ENGINEERING,
    AI_ENGINEERING,
    CYBERSECURITY_ENGINEERING,
    STATISTICS_ENGINEERING,
    OTHER;

    public boolean isOrganizingProgram() {
        return this != OTHER;
    }
}
