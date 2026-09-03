package edu.escuelaing.techcup.players.domain;

public enum JoinRequestStatus {
    PENDING("pendiente"),
    ACCEPTED("aceptada"),
    REJECTED("rechazada"),
    CANCELLED("cancelada");

    private final String label;

    JoinRequestStatus(String label) {
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
}
