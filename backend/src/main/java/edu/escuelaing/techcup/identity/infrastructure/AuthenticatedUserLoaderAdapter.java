package edu.escuelaing.techcup.identity.infrastructure;

import edu.escuelaing.techcup.identity.domain.AppUser;
import edu.escuelaing.techcup.identity.domain.Role;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.security.AuthenticatedUserLoader;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Bridges the shared JWT filter to user persistence (adapter of a shared port). */
@Component
public class AuthenticatedUserLoaderAdapter implements AuthenticatedUserLoader {

    private final AppUserRepository repository;

    public AuthenticatedUserLoaderAdapter(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthenticatedUser> loadActiveUser(Long userId) {
        return repository.findById(userId)
                .filter(AppUser::isActive)
                .map(user -> new AuthenticatedUser(user.getId(), user.getEmail(),
                        user.getRoles().stream().map(Role::name).collect(Collectors.toUnmodifiableSet())));
    }
}
