package edu.escuelaing.techcup.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.escuelaing.techcup.identity.domain.SchoolRelation;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EmailDomainPolicyTest {

    private final EmailDomainPolicy policy =
            new EmailDomainPolicy(List.of("escuelaing.edu.co", " Mail.Escuelaing.edu.co "));

    @Test
    void recognizesInstitutionalDomainsCaseInsensitively() {
        assertThat(policy.isInstitutional("ana@escuelaing.edu.co")).isTrue();
        assertThat(policy.isInstitutional("ana@MAIL.ESCUELAING.EDU.CO")).isTrue();
        assertThat(policy.isInstitutional("ana@gmail.com")).isFalse();
        assertThat(policy.isInstitutional("ana@sub.escuelaing.edu.co")).isFalse();
        assertThat(policy.isInstitutional("not-an-email")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = SchoolRelation.class, names = {"STUDENT", "PROFESSOR", "ADMINISTRATIVE", "GRADUATE"})
    void schoolMembersMustUseInstitutionalEmail(SchoolRelation relation) {
        assertThatCode(() -> policy.validate("ana@escuelaing.edu.co", relation)).doesNotThrowAnyException();
        assertThatThrownBy(() -> policy.validate("ana@gmail.com", relation))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("correo institucional");
    }

    @Test
    void familyMustUsePersonalEmail() {
        assertThatCode(() -> policy.validate("uncle@gmail.com", SchoolRelation.FAMILY)).doesNotThrowAnyException();
        assertThatThrownBy(() -> policy.validate("uncle@escuelaing.edu.co", SchoolRelation.FAMILY))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("correo personal");
    }
}
