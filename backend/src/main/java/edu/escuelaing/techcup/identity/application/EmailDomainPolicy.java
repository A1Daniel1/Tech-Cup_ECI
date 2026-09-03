package edu.escuelaing.techcup.identity.application;

import edu.escuelaing.techcup.identity.domain.SchoolRelation;
import edu.escuelaing.techcup.shared.config.AppProperties;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spec 8: school members (students, professors, staff, graduates) authenticate with an
 * institutional e-mail; family members with a personal one. Institutional domains come from
 * {@code app.institutional-domains}.
 */
@Component
public class EmailDomainPolicy {

    private final List<String> institutionalDomains;

    /**
     * The constructor Spring uses. It must be annotated because the class also exposes the
     * list-based one below for tests: with two candidates and no annotation the container falls
     * back to a default constructor that does not exist.
     */
    @Autowired
    public EmailDomainPolicy(AppProperties properties) {
        this(properties.institutionalDomains());
    }

    /** Directly seedable constructor for tests. */
    public EmailDomainPolicy(List<String> institutionalDomains) {
        this.institutionalDomains = institutionalDomains.stream()
                .map(d -> d.trim().toLowerCase(Locale.ROOT))
                .filter(d -> !d.isEmpty())
                .toList();
    }

    public boolean isInstitutional(String email) {
        int at = email == null ? -1 : email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) {
            return false;
        }
        String domain = email.substring(at + 1).toLowerCase(Locale.ROOT);
        return institutionalDomains.contains(domain);
    }

    /** @throws BusinessRuleException when the e-mail domain does not match the school relation */
    public void validate(String email, SchoolRelation relation) {
        boolean institutional = isInstitutional(email);
        if (relation.requiresInstitutionalEmail() && !institutional) {
            throw new BusinessRuleException("Los estudiantes, profesores, personal administrativo y egresados deben "
                    + "registrarse con un correo institucional (" + String.join(", ", institutionalDomains) + ").");
        }
        if (!relation.requiresInstitutionalEmail() && institutional) {
            throw new BusinessRuleException("Los familiares deben registrarse con un correo personal, no institucional.");
        }
    }
}
