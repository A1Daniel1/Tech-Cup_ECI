package edu.escuelaing.techcup.identity.domain;

/** Relationship of a user with the school; drives the e-mail domain policy. */
public enum SchoolRelation {
    STUDENT,
    PROFESSOR,
    ADMINISTRATIVE,
    GRADUATE,
    FAMILY;

    /** Students, professors, staff and graduates must register with an institutional e-mail. */
    public boolean requiresInstitutionalEmail() {
        return this != FAMILY;
    }
}
