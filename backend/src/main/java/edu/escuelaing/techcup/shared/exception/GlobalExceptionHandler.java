package edu.escuelaing.techcup.shared.exception;

import edu.escuelaing.techcup.shared.storage.InvalidFileException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Translates exceptions into the {@link ApiError} contract. This is the "error handling" duty
 * of the specification's orchestrator, folded into the monolith.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Shown when one or more fields of the request are invalid; the offending fields go in {@code details}. */
    private static final String VALIDATION_FAILED = "La información enviada no es válida. Revise los campos marcados.";

    // --- domain exceptions --------------------------------------------------------------

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler({ConflictException.class, BusinessRuleException.class})
    public ResponseEntity<ApiError> conflict(RuntimeException ex, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler({ForbiddenOperationException.class, AccessDeniedException.class})
    public ResponseEntity<ApiError> forbidden(RuntimeException ex, HttpServletRequest request) {
        String message = ex instanceof ForbiddenOperationException
                ? ex.getMessage()
                : "No tiene permiso para realizar esta acción.";
        return respond(HttpStatus.FORBIDDEN, message, request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> unauthorized(AuthenticationException ex, HttpServletRequest request) {
        return respond(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
    }

    // --- request validation ---------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> beanValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiError.FieldError> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return respond(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, request, details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> constraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<ApiError.FieldError> details = ex.getConstraintViolations().stream()
                .map(cv -> new ApiError.FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();
        return respond(HttpStatus.BAD_REQUEST, VALIDATION_FAILED, request, details);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, MissingServletRequestPartException.class,
            InvalidFileException.class})
    public ResponseEntity<ApiError> badRequest(Exception ex, HttpServletRequest request) {
        String message = ex instanceof HttpMessageNotReadableException
                ? "El cuerpo de la solicitud está mal formado."
                : ex.getMessage();
        return respond(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> uploadTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return respond(HttpStatus.PAYLOAD_TOO_LARGE, "El archivo supera el tamaño máximo permitido.", request, null);
    }

    // --- infrastructure / framework -------------------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation on {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return respond(HttpStatus.CONFLICT, "La solicitud entra en conflicto con la información ya registrada.", request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> noResource(NoResourceFoundException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, "No se encontró el recurso solicitado.", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> methodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                       HttpServletRequest request) {
        return respond(HttpStatus.METHOD_NOT_ALLOWED, "La operación solicitada no está permitida sobre este recurso.", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado. Intente de nuevo más tarde.", request, null);
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, String message, HttpServletRequest request,
                                             List<ApiError.FieldError> details) {
        return ResponseEntity.status(status).body(ApiError.of(status, message, request.getRequestURI(), details));
    }
}
