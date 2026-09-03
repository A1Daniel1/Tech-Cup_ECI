package edu.escuelaing.techcup.competition.api.dto;

import edu.escuelaing.techcup.competition.domain.MatchPhase;
import java.util.List;

/** Matches grouped by phase, in playing order, for the bracket view. */
public record BracketResponse(List<Phase> phases) {

    public record Phase(MatchPhase phase, List<MatchResponse> matches) {
    }
}
