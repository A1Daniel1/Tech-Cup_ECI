package edu.escuelaing.techcup.competition.api.dto;

import java.time.Instant;

/**
 * Rescheduling of a match by an organizer. Every field is optional; only the kick-off time, the
 * venue and the referee can be changed, and only before the current kick-off time.
 */
public record UpdateMatchRequest(Instant scheduledAt, Long venueId, Long refereeId) {
}
