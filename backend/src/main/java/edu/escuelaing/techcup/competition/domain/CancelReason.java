package edu.escuelaing.techcup.competition.domain;

/** Why a scheduled match was cancelled instead of played. */
public enum CancelReason {
    DISQUALIFIED("descalificación"),
    NO_SHOW("no presentación");

    private final String label;

    CancelReason(String label) {
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
