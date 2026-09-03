package edu.escuelaing.techcup.competition.api.dto;

import edu.escuelaing.techcup.competition.domain.Formation;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Lineup submitted by a captain. {@code formation} defaults to {@code F_2_3_1} when omitted and
 * {@code starterIds} must contain exactly {@code Lineup.STARTERS} distinct members of the team.
 */
public record LineupRequest(Formation formation, @NotEmpty(message = "Debe indicar los jugadores titulares.") List<Long> starterIds) {

    public Formation formationOrDefault() {
        return formation == null ? Formation.F_2_3_1 : formation;
    }
}
