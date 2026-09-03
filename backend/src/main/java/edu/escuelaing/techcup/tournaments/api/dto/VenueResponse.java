package edu.escuelaing.techcup.tournaments.api.dto;

import edu.escuelaing.techcup.tournaments.domain.Venue;

public record VenueResponse(Long id, String name, String description, String imageFileId) {

    public static VenueResponse from(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getDescription(), venue.getImageFileId());
    }
}
