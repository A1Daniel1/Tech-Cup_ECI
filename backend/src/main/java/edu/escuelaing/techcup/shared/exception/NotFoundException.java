package edu.escuelaing.techcup.shared.exception;

/** The requested resource does not exist (HTTP 404). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Builds the standard "not found" sentence.
     *
     * @param entity the Spanish noun phrase for the resource, article included, so the sentence
     *               agrees in gender, e.g. {@code "el equipo"} or {@code "la inscripción"}
     */
    public static NotFoundException of(String entity, Object id) {
        return new NotFoundException("No se encontró " + entity + " " + id + ".");
    }
}
