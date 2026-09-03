package edu.escuelaing.techcup.players.api.dto;

import jakarta.validation.constraints.Size;

public record JoinRequestCreateRequest(@Size(max = 500, message = "El mensaje no puede superar los 500 caracteres.") String message) {
}
