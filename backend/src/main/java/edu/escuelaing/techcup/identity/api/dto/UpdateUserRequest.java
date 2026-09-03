package edu.escuelaing.techcup.identity.api.dto;

import edu.escuelaing.techcup.identity.domain.AcademicProgram;
import edu.escuelaing.techcup.identity.domain.SchoolRelation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Editable basic information of a user; e-mail and password are immutable by design. */
public record UpdateUserRequest(
        @NotBlank(message = "El nombre completo es obligatorio.")
        @Size(max = 150, message = "El nombre completo no puede superar los 150 caracteres.")
        String fullName,
        @NotNull(message = "El vínculo con la universidad es obligatorio.")
        SchoolRelation schoolRelation,
        @NotNull(message = "El programa académico es obligatorio.")
        AcademicProgram academicProgram,
        @Min(value = 1, message = "El semestre debe estar entre 1 y 20.")
        @Max(value = 20, message = "El semestre debe estar entre 1 y 20.")
        Integer semester) {
}
