package edu.escuelaing.techcup.identity.application;

import edu.escuelaing.techcup.identity.api.dto.CreateRefereeRequest;
import edu.escuelaing.techcup.identity.api.dto.UserResponse;
import edu.escuelaing.techcup.identity.domain.AppUser;
import edu.escuelaing.techcup.identity.domain.Role;
import edu.escuelaing.techcup.identity.domain.UserStatus;
import edu.escuelaing.techcup.identity.infrastructure.AppUserRepository;
import edu.escuelaing.techcup.shared.audit.AuditAction;
import edu.escuelaing.techcup.shared.audit.AuditService;
import edu.escuelaing.techcup.shared.exception.ConflictException;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Referees are never self-registered: an ORGANIZER (or ADMIN) creates them (spec 5, 7.1).
 * A referee has no school relation, program or semester; e-mail and document stay unique.
 * Audited as REFEREE_CREATED.
 */
@Service
public class RefereeService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuditService auditService;

    public RefereeService(AppUserRepository users, PasswordEncoder passwordEncoder, UserService userService,
                          AuditService auditService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.auditService = auditService;
    }

    @Transactional
    public UserResponse create(AuthenticatedUser actor, CreateRefereeRequest request) {
        String email = AuthService.normalizeEmail(request.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("El correo ya está registrado.");
        }
        if (users.existsByDocumentTypeAndDocumentNumber(request.documentType(), request.documentNumber().trim())) {
            throw new ConflictException("El documento de identidad ya está registrado.");
        }
        AppUser referee = users.save(AppUser.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(UserStatus.ACTIVE)
                .birthDate(request.birthDate())
                .documentType(request.documentType())
                .documentNumber(request.documentNumber().trim())
                .roles(new HashSet<>(Set.of(Role.REFEREE)))
                .build());
        auditService.record(actor.id(), AuditAction.REFEREE_CREATED, UserService.ENTITY_TYPE, referee.getId(),
                Map.of("email", referee.getEmail()));
        return userService.toResponse(referee);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return users.findByRolesContainingOrderByFullNameAsc(Role.REFEREE).stream()
                .map(userService::toResponse)
                .toList();
    }
}
