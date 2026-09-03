package edu.escuelaing.techcup.identity.application;

import edu.escuelaing.techcup.identity.api.dto.LoginRequest;
import edu.escuelaing.techcup.identity.api.dto.LoginResponse;
import edu.escuelaing.techcup.identity.api.dto.RegisterRequest;
import edu.escuelaing.techcup.identity.api.dto.UserResponse;
import edu.escuelaing.techcup.identity.domain.AppUser;
import edu.escuelaing.techcup.identity.domain.DocumentType;
import edu.escuelaing.techcup.identity.domain.Role;
import edu.escuelaing.techcup.identity.domain.SchoolRelation;
import edu.escuelaing.techcup.identity.domain.UserStatus;
import edu.escuelaing.techcup.identity.infrastructure.AppUserRepository;
import edu.escuelaing.techcup.shared.audit.AuditAction;
import edu.escuelaing.techcup.shared.audit.AuditService;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import edu.escuelaing.techcup.shared.exception.ConflictException;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.security.JwtService;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registration and session use cases (spec 7.1). Rules enforced here:
 * <ul>
 *   <li>Self-registration may only pick PLAYER or GUEST as initial role.</li>
 *   <li>Semester is required iff the user is a STUDENT.</li>
 *   <li>E-mail domain must match the school relation ({@link EmailDomainPolicy}).</li>
 *   <li>E-mail and identity document are unique; new accounts start ACTIVE.</li>
 *   <li>Login requires an ACTIVE account and issues a stateless JWT.</li>
 * </ul>
 * Every use case is audited (USER_REGISTERED, LOGIN, LOGOUT).
 */
@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final EmailDomainPolicy emailDomainPolicy;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuditService auditService;

    public AuthService(AppUserRepository users, PasswordEncoder passwordEncoder, EmailDomainPolicy emailDomainPolicy,
                       JwtService jwtService, UserService userService, AuditService auditService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.emailDomainPolicy = emailDomainPolicy;
        this.jwtService = jwtService;
        this.userService = userService;
        this.auditService = auditService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (!request.initialRole().isSelfAssignable()) {
            throw new BusinessRuleException("El rol inicial solo puede ser jugador o invitado.");
        }
        UserService.validateSemester(request.schoolRelation(), request.semester());
        String email = normalizeEmail(request.email());
        emailDomainPolicy.validate(email, request.schoolRelation());
        ensureEmailAndDocumentAvailable(email, request.documentType(), request.documentNumber());

        AppUser user = AppUser.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .schoolRelation(request.schoolRelation())
                .academicProgram(request.academicProgram())
                .semester(request.schoolRelation() == SchoolRelation.STUDENT ? request.semester() : null)
                .status(UserStatus.ACTIVE)
                .birthDate(request.birthDate())
                .documentType(request.documentType())
                .documentNumber(request.documentNumber().trim())
                .roles(new HashSet<>(Set.of(request.initialRole())))
                .build();
        user = users.save(user);

        auditService.record(null, AuditAction.USER_REGISTERED, UserService.ENTITY_TYPE, user.getId(),
                Map.of("email", user.getEmail(), "initialRole", request.initialRole().name()));
        return userService.toResponse(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        AppUser user = users.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("El correo o la contraseña no son correctos."));
        if (!user.isActive()) {
            throw new DisabledException("La cuenta está inactiva. Comuníquese con un administrador.");
        }
        JwtService.IssuedToken token = jwtService.issue(user.getId(), user.getEmail(),
                user.getRoles().stream().map(Role::name).toList());
        auditService.record(user.getId(), AuditAction.LOGIN, UserService.ENTITY_TYPE, user.getId());
        return new LoginResponse(token.token(), token.expiresAt(), userService.toResponse(user));
    }

    /** The token is discarded client-side; the server only records the event. */
    @Transactional
    public void logout(AuthenticatedUser actor) {
        auditService.record(actor.id(), AuditAction.LOGOUT, UserService.ENTITY_TYPE, actor.id());
    }

    @Transactional(readOnly = true)
    public UserResponse me(AuthenticatedUser actor) {
        return userService.getResponse(actor.id());
    }

    private void ensureEmailAndDocumentAvailable(String email, DocumentType documentType, String documentNumber) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("El correo ya está registrado.");
        }
        if (users.existsByDocumentTypeAndDocumentNumber(documentType, documentNumber.trim())) {
            throw new ConflictException("El documento de identidad ya está registrado.");
        }
    }

    static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
