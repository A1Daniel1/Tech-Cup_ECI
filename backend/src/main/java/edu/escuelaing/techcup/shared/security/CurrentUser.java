package edu.escuelaing.techcup.shared.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects the {@link AuthenticatedUser} of the current request into a controller parameter.
 * Resolved by {@link CurrentUserArgumentResolver}; fails with 401 when there is no principal.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
