package edu.escuelaing.techcup.players.api.dto;

import edu.escuelaing.techcup.players.domain.JoinRequestStatus;
import edu.escuelaing.techcup.players.domain.Position;
import java.time.Instant;

public record JoinRequestResponse(
        Long id,
        Long teamId,
        String teamName,
        Long playerId,
        String playerName,
        Position position,
        Integer jerseyNumber,
        JoinRequestStatus status,
        String message,
        Instant createdAt) {
}
