package edu.escuelaing.techcup.competition.domain;

/**
 * Tactical shapes available for a 7-a-side lineup: goalkeeper plus
 * defenders-midfielders-forwards, which always add up to {@value Lineup#STARTERS} players.
 */
public enum Formation {

    F_3_2_1(3, 2, 1),
    /** Default shape used when a captain does not choose one. */
    F_2_3_1(2, 3, 1),
    F_4_1_1(4, 1, 1),
    F_1_3_2(1, 3, 2);

    private final int defenders;
    private final int midfielders;
    private final int forwards;

    Formation(int defenders, int midfielders, int forwards) {
        this.defenders = defenders;
        this.midfielders = midfielders;
        this.forwards = forwards;
    }

    public int defenders() {
        return defenders;
    }

    public int midfielders() {
        return midfielders;
    }

    public int forwards() {
        return forwards;
    }

    /** Goalkeeper included: always {@value Lineup#STARTERS}. */
    public int totalPlayers() {
        return 1 + defenders + midfielders + forwards;
    }
}
