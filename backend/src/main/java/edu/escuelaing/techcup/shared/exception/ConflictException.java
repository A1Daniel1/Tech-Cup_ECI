package edu.escuelaing.techcup.shared.exception;

/** The request clashes with existing state, e.g. a duplicate unique value (HTTP 409). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
