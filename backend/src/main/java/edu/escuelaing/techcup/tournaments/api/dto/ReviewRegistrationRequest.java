package edu.escuelaing.techcup.tournaments.api.dto;

import jakarta.validation.constraints.Size;

/** Optional note an organizer attaches when approving or rejecting a registration. */
public record ReviewRegistrationRequest(@Size(max = 500, message = "La nota no puede superar los 500 caracteres.") String note) {

    public static final ReviewRegistrationRequest EMPTY = new ReviewRegistrationRequest(null);

    public static ReviewRegistrationRequest orEmpty(ReviewRegistrationRequest request) {
        return request == null ? EMPTY : request;
    }
}
