package edu.escuelaing.techcup.tournaments.api.dto;

import edu.escuelaing.techcup.tournaments.domain.Registration;
import edu.escuelaing.techcup.tournaments.domain.RegistrationStatus;
import java.time.Instant;

public record RegistrationResponse(
        Long id,
        Long tournamentId,
        Long teamId,
        String teamName,
        String receiptFileId,
        RegistrationStatus status,
        String reviewNote,
        Instant createdAt,
        Instant reviewedAt) {

    public static RegistrationResponse from(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getTournament().getId(),
                registration.getTeam().getId(),
                registration.getTeam().getName(),
                registration.getReceiptFileId(),
                registration.getStatus(),
                registration.getReviewNote(),
                registration.getCreatedAt(),
                registration.getReviewedAt());
    }
}
