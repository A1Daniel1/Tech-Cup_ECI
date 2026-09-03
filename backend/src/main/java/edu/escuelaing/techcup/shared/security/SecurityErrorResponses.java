package edu.escuelaing.techcup.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.escuelaing.techcup.shared.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Writes the same {@link ApiError} JSON used by the global exception handler for failures that
 * happen inside the security filter chain (before any controller is reached).
 */
@Component
public class SecurityErrorResponses {

    private final ObjectMapper objectMapper;

    public SecurityErrorResponses(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuthenticationEntryPoint entryPoint() {
        return (request, response, ex) ->
                write(request, response, HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) ->
                write(request, response, HttpStatus.FORBIDDEN, "You are not allowed to perform this action");
    }

    private void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
                       String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                ApiError.of(status, message, request.getRequestURI(), null));
    }
}
