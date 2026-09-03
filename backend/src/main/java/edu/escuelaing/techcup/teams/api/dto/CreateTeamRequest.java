package edu.escuelaing.techcup.teams.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank(message = "El nombre del equipo es obligatorio.")
        @Size(max = 100, message = "El nombre del equipo no puede superar los 100 caracteres.")
        String name,
        @NotBlank(message = "Los colores del equipo son obligatorios.")
        @Size(max = 100, message = "Los colores no pueden superar los 100 caracteres.")
        String colors) {
}
