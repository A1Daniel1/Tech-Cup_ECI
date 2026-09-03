package edu.escuelaing.techcup.competition.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The match State machine: a scheduled match is played or cancelled, and then never changes. */
class MatchStatusTest {

    @Test
    void scheduledMovesToPlayedOrCancelled() {
        assertThatCode(() -> MatchStatus.SCHEDULED.transitionTo(MatchStatus.PLAYED)).doesNotThrowAnyException();
        assertThatCode(() -> MatchStatus.SCHEDULED.transitionTo(MatchStatus.CANCELLED)).doesNotThrowAnyException();
    }

    @Test
    void rejectsEveryTransitionOutsideTheMachine() {
        List<MatchStatus> all = List.of(MatchStatus.values());
        for (MatchStatus from : all) {
            for (MatchStatus to : all) {
                if (from.canTransitionTo(to)) {
                    continue;
                }
                assertThatThrownBy(() -> from.transitionTo(to))
                        .as("%s -> %s must be rejected", from, to)
                        .isInstanceOf(BusinessRuleException.class);
            }
        }
    }

    @Test
    void aPlayedMatchCannotBeCancelledOrReplayed() {
        assertThat(MatchStatus.PLAYED.allowedTargets()).isEmpty();
        assertThat(MatchStatus.CANCELLED.allowedTargets()).isEmpty();
        assertThat(MatchStatus.PLAYED.isFinished()).isTrue();
        assertThat(MatchStatus.CANCELLED.isFinished()).isTrue();
        assertThat(MatchStatus.SCHEDULED.isFinished()).isFalse();
    }
}
