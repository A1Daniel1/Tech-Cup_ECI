package edu.escuelaing.techcup.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.escuelaing.techcup.identity.api.dto.UpdateUserRequest;
import edu.escuelaing.techcup.identity.domain.AcademicProgram;
import edu.escuelaing.techcup.identity.domain.AppUser;
import edu.escuelaing.techcup.identity.domain.SchoolRelation;
import edu.escuelaing.techcup.identity.domain.UserStatus;
import edu.escuelaing.techcup.identity.infrastructure.AppUserRepository;
import edu.escuelaing.techcup.shared.audit.AuditAction;
import edu.escuelaing.techcup.shared.audit.AuditService;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import edu.escuelaing.techcup.shared.exception.ForbiddenOperationException;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final AuthenticatedUser ADMIN = new AuthenticatedUser(1L, "admin@escuelaing.edu.co", Set.of("ADMIN"));
    private static final AuthenticatedUser PLAYER = new AuthenticatedUser(10L, "ana@escuelaing.edu.co", Set.of("PLAYER"));

    @Mock
    private AppUserRepository users;
    @Mock
    private UserFactsPort userFacts;
    @Mock
    private AuditService auditService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(users, userFacts, new EmailDomainPolicy(List.of("escuelaing.edu.co")),
                auditService);
    }

    @Test
    void inactivationIsRefusedWhileLockedByATournament() {
        when(users.findById(10L)).thenReturn(Optional.of(activeUser(10L)));
        when(userFacts.isLockedByTournament(10L)).thenReturn(true);

        assertThatThrownBy(() -> userService.inactivate(ADMIN, 10L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("torneo activo o en progreso");
    }

    @Test
    void inactivatesAnUnlockedUser() {
        AppUser user = activeUser(10L);
        when(users.findById(10L)).thenReturn(Optional.of(user));
        when(userFacts.isLockedByTournament(10L)).thenReturn(false);
        when(userFacts.activeTeamIdOf(anyLong())).thenReturn(Optional.empty());

        userService.inactivate(ADMIN, 10L);

        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        verify(auditService).record(eq(1L), eq(AuditAction.USER_INACTIVATED), anyString(), eq(10L));
    }

    @Test
    void adminCannotInactivateThemself() {
        assertThatThrownBy(() -> userService.inactivate(ADMIN, 1L)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void usersCanOnlyUpdateTheirOwnBasicInfo() {
        UpdateUserRequest request = new UpdateUserRequest("Other", SchoolRelation.STUDENT,
                AcademicProgram.AI_ENGINEERING, 4);

        assertThatThrownBy(() -> userService.updateBasicInfo(PLAYER, 11L, request))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void updateKeepsSemesterConsistentWithSchoolRelation() {
        when(users.findById(10L)).thenReturn(Optional.of(activeUser(10L)));

        assertThatThrownBy(() -> userService.updateBasicInfo(PLAYER, 10L,
                new UpdateUserRequest("Ana", SchoolRelation.GRADUATE, AcademicProgram.AI_ENGINEERING, 4)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("solo aplica para los estudiantes");
    }

    @Test
    void updateKeepsEmailConsistentWithSchoolRelation() {
        when(users.findById(10L)).thenReturn(Optional.of(activeUser(10L)));

        assertThatThrownBy(() -> userService.updateBasicInfo(PLAYER, 10L,
                new UpdateUserRequest("Ana", SchoolRelation.FAMILY, AcademicProgram.OTHER, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("correo personal");
    }

    private static AppUser activeUser(Long id) {
        return AppUser.builder()
                .id(id)
                .fullName("Ana Diaz")
                .email("ana@escuelaing.edu.co")
                .schoolRelation(SchoolRelation.STUDENT)
                .academicProgram(AcademicProgram.SYSTEMS_ENGINEERING)
                .semester(5)
                .status(UserStatus.ACTIVE)
                .roles(new HashSet<>())
                .build();
    }
}
