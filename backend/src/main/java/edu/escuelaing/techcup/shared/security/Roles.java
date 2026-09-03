package edu.escuelaing.techcup.shared.security;

/**
 * Role names as used by Spring Security expressions ({@code hasRole('ADMIN')}).
 * The canonical enumeration lives in {@code identity.domain.Role}; these constants only
 * avoid magic strings in the shared security layer.
 */
public final class Roles {

    public static final String GUEST = "GUEST";
    public static final String PLAYER = "PLAYER";
    public static final String CAPTAIN = "CAPTAIN";
    public static final String ORGANIZER = "ORGANIZER";
    public static final String REFEREE = "REFEREE";
    public static final String ADMIN = "ADMIN";

    public static final String AUTHORITY_PREFIX = "ROLE_";

    private Roles() {
    }

    public static String authority(String role) {
        return AUTHORITY_PREFIX + role;
    }
}
