package edu.escuelaing.techcup.players.api;

import edu.escuelaing.techcup.players.api.dto.JoinRequestCreateRequest;
import edu.escuelaing.techcup.players.api.dto.JoinRequestResponse;
import edu.escuelaing.techcup.players.application.JoinRequestService;
import edu.escuelaing.techcup.players.domain.JoinRequestStatus;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Join requests")
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    public JoinRequestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @PostMapping("/teams/{teamId}/join-requests")
    @PreAuthorize("hasRole('PLAYER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ask to join a team (one pending request at a time)")
    public JoinRequestResponse create(@CurrentUser AuthenticatedUser actor, @PathVariable Long teamId,
                                      @Valid @RequestBody(required = false) JoinRequestCreateRequest request) {
        return joinRequestService.create(actor, teamId, request);
    }

    @GetMapping("/players/me/join-requests")
    @PreAuthorize("hasRole('PLAYER')")
    public List<JoinRequestResponse> mine(@CurrentUser AuthenticatedUser actor) {
        return joinRequestService.listMine(actor);
    }

    @PostMapping("/join-requests/{id}/cancel")
    @PreAuthorize("hasRole('PLAYER')")
    public JoinRequestResponse cancel(@CurrentUser AuthenticatedUser actor, @PathVariable Long id) {
        return joinRequestService.cancel(actor, id);
    }

    @GetMapping("/teams/{teamId}/join-requests")
    @PreAuthorize("hasRole('CAPTAIN')")
    @Operation(summary = "Requests received by my team, optionally filtered by status")
    public List<JoinRequestResponse> forTeam(@CurrentUser AuthenticatedUser actor, @PathVariable Long teamId,
                                             @RequestParam(required = false) JoinRequestStatus status) {
        return joinRequestService.listForTeam(actor, teamId, status);
    }

    @PostMapping("/join-requests/{id}/accept")
    @PreAuthorize("hasRole('CAPTAIN')")
    public JoinRequestResponse accept(@CurrentUser AuthenticatedUser actor, @PathVariable Long id) {
        return joinRequestService.accept(actor, id);
    }

    @PostMapping("/join-requests/{id}/reject")
    @PreAuthorize("hasRole('CAPTAIN')")
    public JoinRequestResponse reject(@CurrentUser AuthenticatedUser actor, @PathVariable Long id) {
        return joinRequestService.reject(actor, id);
    }
}
