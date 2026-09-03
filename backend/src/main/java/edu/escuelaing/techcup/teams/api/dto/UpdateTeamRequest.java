package edu.escuelaing.techcup.teams.api.dto;

import jakarta.validation.constraints.Size;

/** Partial update: any field left null is kept as is. */
public record UpdateTeamRequest(
        @Size(max = 100, message = "El nombre del equipo no puede superar los 100 caracteres.")
        String name,
        @Size(max = 100, message = "Los colores no pueden superar los 100 caracteres.")
        String colors) {
}
