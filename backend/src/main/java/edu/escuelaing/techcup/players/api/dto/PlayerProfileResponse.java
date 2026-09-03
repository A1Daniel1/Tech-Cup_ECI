package edu.escuelaing.techcup.players.api.dto;

import edu.escuelaing.techcup.players.domain.Position;

public record PlayerProfileResponse(
        Long userId,
        String fullName,
        Position position,
        int jerseyNumber,
        String photoFileId,
        Long teamId,
        String teamName) {
}
