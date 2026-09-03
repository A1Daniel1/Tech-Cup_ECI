package edu.escuelaing.techcup.shared.exception;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Error body returned by every failing request:
 * {@code { timestamp, status, error, message, path, details?: [{field, message}] }}.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> details) {

    public record FieldError(String field, String message) {
    }

    public static ApiError of(HttpStatus status, String message, String path, List<FieldError> details) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path,
                details == null || details.isEmpty() ? null : details);
    }
}
