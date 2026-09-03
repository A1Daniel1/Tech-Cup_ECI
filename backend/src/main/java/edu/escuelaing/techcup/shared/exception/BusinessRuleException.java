package edu.escuelaing.techcup.shared.exception;

/**
 * A business rule of the specification was violated (HTTP 409). The message explains the rule
 * in plain Spanish so the frontend can show it verbatim to the end user.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
