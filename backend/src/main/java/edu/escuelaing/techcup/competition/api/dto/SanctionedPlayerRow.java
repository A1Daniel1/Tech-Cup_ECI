package edu.escuelaing.techcup.competition.api.dto;

/** A player who may not take part in a match, with the reason a referee can read out. */
public record SanctionedPlayerRow(Long userId, String fullName, Long teamId, String teamName, String reason) {
}
