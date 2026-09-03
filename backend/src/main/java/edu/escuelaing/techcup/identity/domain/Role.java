package edu.escuelaing.techcup.identity.domain;

/** Platform roles. A user may hold several; {@code ADMIN} implies all of them (role hierarchy). */
public enum Role {
    GUEST("invitado"),
    PLAYER("jugador"),
    CAPTAIN("capitán"),
    ORGANIZER("organizador"),
    REFEREE("árbitro"),
    ADMIN("administrador");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    /**
     * Spanish name of this constant, for the user-facing messages the frontend shows verbatim.
     * Every enum that reaches such a message exposes the same {@code label()} accessor, so the
     * wording lives with the constant instead of in a switch somewhere in the service layer.
     */
    public String label() {
        return label;
    }

    /** Roles a user may pick for themselves when registering. */
    public boolean isSelfAssignable() {
        return this == PLAYER || this == GUEST;
    }
}
