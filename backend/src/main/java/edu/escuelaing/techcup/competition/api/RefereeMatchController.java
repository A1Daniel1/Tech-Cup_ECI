package edu.escuelaing.techcup.competition.api;

import edu.escuelaing.techcup.competition.api.dto.MatchResponse;
import edu.escuelaing.techcup.competition.application.MatchService;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** What a referee sees of the competition: the matches they have been appointed to. */
@RestController
@RequestMapping("/api/referees")
@Tag(name = "Competition")
public class RefereeMatchController {

    private final MatchService matchService;

    public RefereeMatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/me/matches")
    @PreAuthorize("hasRole('REFEREE')")
    @Operation(summary = "Matches I have been appointed to, in playing order")
    public List<MatchResponse> myMatches(@CurrentUser AuthenticatedUser actor) {
        return matchService.refereeMatches(actor);
    }
}
