package edu.escuelaing.techcup.identity.application;

import edu.escuelaing.techcup.identity.api.dto.UpdateUserRequest;
import edu.escuelaing.techcup.identity.api.dto.UserResponse;
import edu.escuelaing.techcup.identity.domain.AppUser;
import edu.escuelaing.techcup.identity.domain.Role;
import edu.escuelaing.techcup.identity.domain.SchoolRelation;
import edu.escuelaing.techcup.identity.domain.UserStatus;
import edu.escuelaing.techcup.identity.infrastructure.AppUserRepository;
import edu.escuelaing.techcup.shared.audit.AuditAction;
import edu.escuelaing.techcup.shared.audit.AuditService;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import edu.escuelaing.techcup.shared.exception.ForbiddenOperationException;
import edu.escuelaing.techcup.shared.exception.NotFoundException;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Queries and lifecycle of user accounts. Rules enforced here:
 * <ul>
 *   <li>Basic info (name, school relation, program, semester) is editable by the user or an
 *       ADMIN; e-mail and password are immutable. Semester is required iff STUDENT and the
 *       (immutable) e-mail must stay consistent with the new school relation.</li>
 *   <li>Inactivation is refused while the user belongs to a team with an APPROVED registration
 *       in an ACTIVE or IN_PROGRESS tournament.</li>
 * </ul>
 */
@Service
public class UserService {

    static final String ENTITY_TYPE = "USER";

    private final AppUserRepository users;
    private final UserFactsPort userFacts;
    private final EmailDomainPolicy emailDomainPolicy;
    private final AuditService auditService;

    public UserService(AppUserRepository users, UserFactsPort userFacts, EmailDomainPolicy emailDomainPolicy,
                       AuditService auditService) {
        this.users = users;
        this.userFacts = userFacts;
        this.emailDomainPolicy = emailDomainPolicy;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public AppUser getUser(Long id) {
        return users.findById(id).orElseThrow(() -> NotFoundException.of("el usuario", id));
    }

    @Transactional(readOnly = true)
    public UserResponse getResponse(Long id) {
        return toResponse(getUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> search(String term) {
        return users.search(term == null ? "" : term.trim()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserResponse updateBasicInfo(AuthenticatedUser actor, Long userId, UpdateUserRequest request) {
        if (!actor.isAdmin() && !actor.id().equals(userId)) {
            throw new ForbiddenOperationException("Solo puede modificar su propia cuenta.");
        }
        AppUser user = getUser(userId);
        validateSemester(request.schoolRelation(), request.semester());
        emailDomainPolicy.validate(user.getEmail(), request.schoolRelation());

        user.setFullName(request.fullName().trim());
        user.setSchoolRelation(request.schoolRelation());
        user.setAcademicProgram(request.academicProgram());
        user.setSemester(request.schoolRelation() == SchoolRelation.STUDENT ? request.semester() : null);

        auditService.record(actor.id(), AuditAction.USER_UPDATED, ENTITY_TYPE, user.getId(),
                Map.of("fullName", user.getFullName(),
                        "schoolRelation", user.getSchoolRelation().name(),
                        "academicProgram", user.getAcademicProgram().name()));
        return toResponse(user);
    }

    @Transactional
    public UserResponse inactivate(AuthenticatedUser actor, Long userId) {
        if (actor.id().equals(userId)) {
            throw new BusinessRuleException("No puede inactivar su propia cuenta.");
        }
        AppUser user = getUser(userId);
        if (!user.isActive()) {
            throw new BusinessRuleException("El usuario ya está inactivo.");
        }
        if (userFacts.isLockedByTournament(userId)) {
            throw new BusinessRuleException("El usuario pertenece a un equipo inscrito en un torneo activo "
                    + "o en progreso, por lo que no se puede inactivar.");
        }
        user.setStatus(UserStatus.INACTIVE);
        auditService.record(actor.id(), AuditAction.USER_INACTIVATED, ENTITY_TYPE, user.getId());
        return toResponse(user);
    }

    /** Semester is mandatory for students and meaningless for everybody else. */
    static void validateSemester(SchoolRelation relation, Integer semester) {
        if (relation == SchoolRelation.STUDENT && semester == null) {
            throw new BusinessRuleException("El semestre es obligatorio para los estudiantes.");
        }
        if (relation != SchoolRelation.STUDENT && semester != null) {
            throw new BusinessRuleException("El semestre solo aplica para los estudiantes.");
        }
    }

    public UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getSchoolRelation(),
                user.getAcademicProgram(),
                user.getSemester(),
                user.getStatus(),
                user.getBirthDate(),
                user.getDocumentType(),
                user.getDocumentNumber(),
                user.getRoles().stream().sorted().map(Role::name).toList(),
                userFacts.hasSportProfile(user.getId()),
                userFacts.activeTeamIdOf(user.getId()).orElse(null));
    }
}
