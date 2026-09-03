package edu.escuelaing.techcup.shared.exception;

/** The actor is authenticated but not allowed to act on this resource (HTTP 403). */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
