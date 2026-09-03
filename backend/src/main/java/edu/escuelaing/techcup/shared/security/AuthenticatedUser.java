package edu.escuelaing.techcup.shared.security;

import java.util.Set;

/**
 * The principal stored in the {@link org.springframework.security.core.context.SecurityContext}
 * for every authenticated request. Deliberately small and immutable: services receive it as the
 * "actor" of a use case, which keeps them trivial to unit-test.
 */
public record AuthenticatedUser(Long id, String email, Set<String> roles) {

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(Roles.ADMIN);
    }
}
