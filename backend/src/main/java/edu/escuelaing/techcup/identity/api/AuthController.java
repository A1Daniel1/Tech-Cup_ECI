package edu.escuelaing.techcup.identity.api;

import edu.escuelaing.techcup.identity.api.dto.LoginRequest;
import edu.escuelaing.techcup.identity.api.dto.LoginResponse;
import edu.escuelaing.techcup.identity.api.dto.RegisterRequest;
import edu.escuelaing.techcup.identity.api.dto.UserResponse;
import edu.escuelaing.techcup.identity.application.AuthService;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Self-registration as PLAYER or GUEST")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Authenticate with e-mail and password, returns a JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Audit the logout; the client discards the token")
    public void logout(@CurrentUser AuthenticatedUser actor) {
        authService.logout(actor);
    }

    @GetMapping("/me")
    @Operation(summary = "Current user")
    public UserResponse me(@CurrentUser AuthenticatedUser actor) {
        return authService.me(actor);
    }
}
