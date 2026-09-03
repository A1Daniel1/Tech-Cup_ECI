package edu.escuelaing.techcup.competition.api.dto;

/** One entry of the top-scorers table, ordered by goals descending. */
public record TopScorerRow(Long playerId, String playerName, Long teamId, String teamName, long goals) {
}
