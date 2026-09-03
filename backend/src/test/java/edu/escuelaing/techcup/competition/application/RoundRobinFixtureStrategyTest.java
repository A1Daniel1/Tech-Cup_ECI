package edu.escuelaing.techcup.competition.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edu.escuelaing.techcup.competition.application.FixtureStrategy.Fixture;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** The circle-method group-stage draw: completeness, matchday shape and reproducibility. */
class RoundRobinFixtureStrategyTest {

    private static final long SEED = 42L;

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 11, 12})
    void everyTeamMeetsEveryOtherTeamExactlyOnce(int teamCount) {
        List<Long> teams = teams(teamCount);

        List<Fixture> fixtures = seededStrategy().generate(teams);

        assertThat(fixtures).hasSize(teamCount * (teamCount - 1) / 2);
        Set<Set<Long>> pairings = fixtures.stream()
                .map(fixture -> Set.of(fixture.homeTeamId(), fixture.awayTeamId()))
                .collect(Collectors.toCollection(HashSet::new));
        assertThat(pairings).hasSize(fixtures.size());
        for (Long first : teams) {
            for (Long second : teams) {
                if (!first.equals(second)) {
                    assertThat(pairings).contains(Set.of(first, second));
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 6, 8})
    void anEvenDrawGivesEveryTeamOneMatchPerRound(int teamCount) {
        List<Fixture> fixtures = seededStrategy().generate(teams(teamCount));

        Map<Integer, List<Fixture>> rounds = fixtures.stream()
                .collect(Collectors.groupingBy(Fixture::roundNumber));
        assertThat(rounds).hasSize(teamCount - 1);
        rounds.values().forEach(round -> {
            assertThat(round).hasSize(teamCount / 2);
            Set<Long> playing = round.stream()
                    .flatMap(fixture -> List.of(fixture.homeTeamId(), fixture.awayTeamId()).stream())
                    .collect(Collectors.toSet());
            assertThat(playing).hasSize(teamCount);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 7})
    void anOddDrawRestsExactlyOneTeamPerRound(int teamCount) {
        List<Fixture> fixtures = seededStrategy().generate(teams(teamCount));

        Map<Integer, List<Fixture>> rounds = fixtures.stream()
                .collect(Collectors.groupingBy(Fixture::roundNumber));
        assertThat(rounds).hasSize(teamCount);
        rounds.values().forEach(round -> assertThat(round).hasSize((teamCount - 1) / 2));
    }

    @Test
    void noTeamEverPlaysItself() {
        seededStrategy().generate(teams(7))
                .forEach(fixture -> assertThat(fixture.homeTeamId()).isNotEqualTo(fixture.awayTeamId()));
    }

    @Test
    void theSameSeedAlwaysProducesTheSameDraw() {
        List<Fixture> first = seededStrategy().generate(teams(6));
        List<Fixture> second = seededStrategy().generate(teams(6));

        assertThat(first).isEqualTo(second);
    }

    @Test
    void adifferentSeedProducesAdifferentDraw() {
        List<Fixture> first = seededStrategy().generate(teams(8));
        List<Fixture> second = new RoundRobinFixtureStrategy(new FixtureShuffler(new Random(7L)))
                .generate(teams(8));

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void homeAdvantageIsNotAlwaysGivenToTheSameTeam() {
        List<Fixture> fixtures = seededStrategy().generate(teams(6));

        Map<Long, Long> homeAppearances = fixtures.stream()
                .collect(Collectors.groupingBy(Fixture::homeTeamId, Collectors.counting()));
        assertThat(homeAppearances.values()).allSatisfy(count -> assertThat(count).isLessThan(5L));
    }

    @Test
    void aSingleTeamCannotFormAGroupStage() {
        assertThatThrownBy(() -> seededStrategy().generate(teams(1)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("al menos 2 equipos");
    }

    @Test
    void theInputListIsNeverModified() {
        List<Long> teams = teams(5);
        List<Long> snapshot = List.copyOf(teams);

        seededStrategy().generate(teams);

        assertThat(teams).isEqualTo(snapshot);
    }

    private static RoundRobinFixtureStrategy seededStrategy() {
        return new RoundRobinFixtureStrategy(new FixtureShuffler(new Random(SEED)));
    }

    private static List<Long> teams(int count) {
        return LongStream.rangeClosed(1, count).boxed().toList();
    }
}
