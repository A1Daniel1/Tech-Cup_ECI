package edu.escuelaing.techcup.tournaments.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** The registration State machine: only a review in progress can still change. */
class RegistrationStatusTest {

    @ParameterizedTest
    @EnumSource(value = RegistrationStatus.class, names = {"APPROVED", "REJECTED", "CANCELLED"})
    void underReviewMovesToEveryTerminalStatus(RegistrationStatus target) {
        assertThatCode(() -> RegistrationStatus.UNDER_REVIEW.transitionTo(target)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = RegistrationStatus.class, names = {"APPROVED", "REJECTED", "CANCELLED"})
    void terminalStatusesNeverChangeAgain(RegistrationStatus terminal) {
        assertThat(terminal.allowedTargets()).isEmpty();
        for (RegistrationStatus target : RegistrationStatus.values()) {
            assertThatThrownBy(() -> terminal.transitionTo(target))
                    .as("%s -> %s must be rejected", terminal, target)
                    .isInstanceOf(BusinessRuleException.class);
        }
    }

    @Test
    void rejectsEveryTransitionOutsideTheMachine() {
        List<RegistrationStatus> all = List.of(RegistrationStatus.values());
        for (RegistrationStatus from : all) {
            for (RegistrationStatus to : all) {
                if (from.canTransitionTo(to)) {
                    continue;
                }
                assertThatThrownBy(() -> from.transitionTo(to))
                        .isInstanceOf(BusinessRuleException.class)
                        .hasMessageContaining(from.label());
            }
        }
    }

    @Test
    void liveStatusesOccupyTheTeamSlot() {
        assertThat(RegistrationStatus.UNDER_REVIEW.isLive()).isTrue();
        assertThat(RegistrationStatus.APPROVED.isLive()).isTrue();
        assertThat(RegistrationStatus.REJECTED.isLive()).isFalse();
        assertThat(RegistrationStatus.CANCELLED.isLive()).isFalse();
    }
}
