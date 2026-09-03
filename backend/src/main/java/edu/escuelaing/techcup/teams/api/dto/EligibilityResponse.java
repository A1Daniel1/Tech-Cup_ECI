package edu.escuelaing.techcup.teams.api.dto;

import edu.escuelaing.techcup.teams.domain.TeamEligibility;
import java.util.List;

public record EligibilityResponse(boolean eligible, List<String> problems) {

    public static EligibilityResponse from(TeamEligibility.Result result) {
        return new EligibilityResponse(result.eligible(), result.problems());
    }
}
