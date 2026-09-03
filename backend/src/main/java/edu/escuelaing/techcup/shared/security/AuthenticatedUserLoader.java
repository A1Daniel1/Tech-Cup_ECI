package edu.escuelaing.techcup.shared.security;

import java.util.Optional;

/**
 * Port used by {@link JwtAuthenticationFilter} to turn a token subject into a principal.
 * Implemented by the identity module, so the shared layer never depends on user persistence.
 */
public interface AuthenticatedUserLoader {

    /** Returns the principal for an ACTIVE user, empty if unknown or inactive. */
    Optional<AuthenticatedUser> loadActiveUser(Long userId);
}
