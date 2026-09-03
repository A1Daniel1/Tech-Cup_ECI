package edu.escuelaing.techcup.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.escuelaing.techcup.identity.api.dto.RegisterRequest;
import edu.escuelaing.techcup.identity.domain.AcademicProgram;
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
import edu.escuelaing.techcup.shared.security.JwtService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository users;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private AuditService auditService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        EmailDomainPolicy policy = new EmailDomainPolicy(List.of("escuelaing.edu.co"));
        authService = new AuthService(users, passwordEncoder, policy, jwtService, userService, auditService);
    }

    @Test
    void registersAnActiveStudentWithTheChosenRole() {
        when(users.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(users.existsByDocumentTypeAndDocumentNumber(any(), anyString())).thenReturn(false);
        when(passwordEncoder.encode("Secret123*")).thenReturn("hash");
        when(users.save(any())).thenAnswer(inv -> {
            AppUser u = inv.getArgument(0);
            u.setId(7L);
            return u;
        });

        authService.register(request(SchoolRelation.STUDENT, 5, "Ana@Escuelaing.edu.co", Role.PLAYER));

        ArgumentCaptor<AppUser> saved = ArgumentCaptor.forClass(AppUser.class);
        verify(users).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("ana@escuelaing.edu.co");
        assertThat(saved.getValue().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.getValue().getRoles()).containsExactly(Role.PLAYER);
        assertThat(saved.getValue().getSemester()).isEqualTo(5);
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("hash");
        verify(auditService).record(eq(null), eq(AuditAction.USER_REGISTERED), anyString(), eq(7L), any());
    }

    @Test
    void semesterIsRequiredForStudents() {
        assertThatThrownBy(() -> authService.register(
                request(SchoolRelation.STUDENT, null, "ana@escuelaing.edu.co", Role.PLAYER)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El semestre es obligatorio");
        verify(users, never()).save(any());
    }

    @Test
    void semesterIsRejectedForNonStudents() {
        assertThatThrownBy(() -> authService.register(
                request(SchoolRelation.PROFESSOR, 3, "ana@escuelaing.edu.co", Role.PLAYER)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("solo aplica para los estudiantes");
    }

    @Test
    void initialRoleMustBePlayerOrGuest() {
        assertThatThrownBy(() -> authService.register(
                request(SchoolRelation.STUDENT, 5, "ana@escuelaing.edu.co", Role.ORGANIZER)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("jugador o invitado");
    }

    @Test
    void appliesEmailDomainPolicy() {
        assertThatThrownBy(() -> authService.register(
                request(SchoolRelation.FAMILY, null, "uncle@escuelaing.edu.co", Role.GUEST)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void rejectsDuplicateEmail() {
        when(users.existsByEmailIgnoreCase("ana@escuelaing.edu.co")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                request(SchoolRelation.STUDENT, 5, "ana@escuelaing.edu.co", Role.PLAYER)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("correo");
    }

    private static RegisterRequest request(SchoolRelation relation, Integer semester, String email, Role role) {
        return new RegisterRequest("Ana Diaz", email, "Secret123*", relation, AcademicProgram.SYSTEMS_ENGINEERING,
                semester, LocalDate.of(2002, 3, 4), DocumentType.CC, "1001", role);
    }
}
