package edu.escuelaing.techcup.players.api.dto;

import edu.escuelaing.techcup.players.domain.Position;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayerProfileRequest(
        @NotNull(message = "La posición es obligatoria.")
        Position position,
        @NotNull(message = "El número de camiseta es obligatorio.")
        @Min(value = 1, message = "El número de camiseta debe estar entre 1 y 99.")
        @Max(value = 99, message = "El número de camiseta debe estar entre 1 y 99.")
        Integer jerseyNumber) {
}
