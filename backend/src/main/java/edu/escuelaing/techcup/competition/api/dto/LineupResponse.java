package edu.escuelaing.techcup.competition.api.dto;

import edu.escuelaing.techcup.competition.domain.Formation;
import edu.escuelaing.techcup.players.domain.Position;
import java.util.List;

public record LineupResponse(
        Long matchId,
        Long teamId,
        Formation formation,
        List<Player> starters,
        List<Player> substitutes) {

    public record Player(Long userId, String fullName, Position position, Integer jerseyNumber) {
    }
}
