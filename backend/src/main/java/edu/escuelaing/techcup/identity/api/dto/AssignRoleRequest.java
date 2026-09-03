package edu.escuelaing.techcup.identity.api.dto;

import edu.escuelaing.techcup.identity.domain.Role;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(@NotNull(message = "El rol es obligatorio.") Role role) {
}
