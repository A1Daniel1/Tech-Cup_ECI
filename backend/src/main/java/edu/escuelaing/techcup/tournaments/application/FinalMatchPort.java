package edu.escuelaing.techcup.tournaments.application;

/**
 * The single fact the tournaments module needs from the competition module: whether the FINAL
 * match of a tournament has already been played, which is one of the two ways of finishing a
 * tournament. Expressed as a port so tournaments never depends on competition code — competition
 * already depends on tournaments, and a direct call back would create a cycle.
 */
public interface FinalMatchPort {

    boolean isFinalMatchPlayed(Long tournamentId);
}
