package edu.escuelaing.techcup.shared.home;

import edu.escuelaing.techcup.competition.api.dto.MatchResponse;
import edu.escuelaing.techcup.competition.api.dto.StandingRow;
import edu.escuelaing.techcup.teams.api.dto.TeamResponse;
import edu.escuelaing.techcup.tournaments.api.dto.RegistrationResponse;
import edu.escuelaing.techcup.tournaments.api.dto.TournamentResponse;
import java.util.List;

/**
 * Landing-page aggregate shown after login: what is going on in the tournament right now and
 * where the caller stands in it. Every field is null or empty when it does not apply (no live
 * tournament, no team, no registration).
 */
public record HomeResponse(
        TournamentResponse tournament,
        TeamResponse myTeam,
        RegistrationResponse myRegistration,
        List<MatchResponse> upcomingMatches,
        List<StandingRow> standingsTop) {
}
