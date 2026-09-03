package edu.escuelaing.techcup.shared.storage;

/**
 * An uploaded file is empty, too large or of an unsupported content type (HTTP 400). The message
 * is written in Spanish because the frontend shows it verbatim to the end user.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}
