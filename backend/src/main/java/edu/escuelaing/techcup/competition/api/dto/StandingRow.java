package edu.escuelaing.techcup.competition.api.dto;

import edu.escuelaing.techcup.competition.domain.Standings;

/** One row of the group-stage table. */
public record StandingRow(
        int position,
        Long teamId,
        String teamName,
        int played,
        int won,
        int drawn,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        int points) {

    public static StandingRow from(Standings.Row row) {
        return new StandingRow(row.position(), row.teamId(), row.teamName(), row.played(), row.won(),
                row.drawn(), row.lost(), row.goalsFor(), row.goalsAgainst(), row.goalDifference(), row.points());
    }
}
