package edu.escuelaing.techcup.competition.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.escuelaing.techcup.competition.application.FixtureStrategy.Fixture;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The bracket fold: the best seed always meets the worst one. */
class KnockoutFixtureStrategyTest {

    private final KnockoutFixtureStrategy strategy = new KnockoutFixtureStrategy();

    @Test
    void eightTeamsSeedTheQuarterFinalsOneVersusEight() {
        List<Fixture> fixtures = strategy.generate(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));

        assertThat(fixtures).containsExactly(
                new Fixture(1, 1L, 8L),
                new Fixture(1, 2L, 7L),
                new Fixture(1, 3L, 6L),
                new Fixture(1, 4L, 5L));
    }

    @Test
    void fourTeamsSeedTheSemiFinals() {
        List<Fixture> fixtures = strategy.generate(List.of(10L, 20L, 30L, 40L));

        assertThat(fixtures).containsExactly(
                new Fixture(1, 10L, 40L),
                new Fixture(1, 20L, 30L));
    }

    @Test
    void twoTeamsSeedTheFinal() {
        List<Fixture> fixtures = strategy.generate(List.of(3L, 9L));

        assertThat(fixtures).containsExactly(new Fixture(1, 3L, 9L));
    }

    @Test
    void winnersAdvanceWithTheSameFold() {
        // Winners of QF1..QF4 in bracket order produce the classic semi-final pairing.
        List<Fixture> fixtures = strategy.generate(List.of(1L, 2L, 3L, 4L));

        assertThat(fixtures).containsExactly(
                new Fixture(1, 1L, 4L),
                new Fixture(1, 2L, 3L));
    }

    @Test
    void everyKnockoutPhaseIsASingleRound() {
        assertThat(strategy.generate(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)))
                .allSatisfy(fixture -> assertThat(fixture.roundNumber()).isEqualTo(1));
    }

    @Test
    void anOddNumberOfTeamsCannotFormABracket() {
        assertThatThrownBy(() -> strategy.generate(List.of(1L, 2L, 3L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("número par de equipos");
    }

    @Test
    void asingleTeamCannotFormABracket() {
        assertThatThrownBy(() -> strategy.generate(List.of(1L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("al menos 2 equipos");
    }
}
