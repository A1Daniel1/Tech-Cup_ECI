package edu.escuelaing.techcup.competition.domain;

import static org.assertj.core.api.Assertions.assertThat;

import edu.escuelaing.techcup.competition.domain.Standings.Competitor;
import edu.escuelaing.techcup.competition.domain.Standings.Result;
import edu.escuelaing.techcup.competition.domain.Standings.Row;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The group table computed from a hand-built set of results. */
class StandingsTest {

    private static final List<Competitor> FOUR_TEAMS = List.of(
            new Competitor(1L, "Alpha"),
            new Competitor(2L, "Bravo"),
            new Competitor(3L, "Charlie"),
            new Competitor(4L, "Delta"));

    @Test
    void awardsThreePointsForAWinOneForADrawAndNoneForALoss() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of(
                new Result(1L, 2L, 3, 1),   // Alpha beats Bravo
                new Result(3L, 4L, 2, 2))); // Charlie draws with Delta

        assertThat(row(rows, 1L).points()).isEqualTo(3);
        assertThat(row(rows, 2L).points()).isZero();
        assertThat(row(rows, 3L).points()).isEqualTo(1);
        assertThat(row(rows, 4L).points()).isEqualTo(1);
    }

    @Test
    void countsPlayedWonDrawnLostAndGoals() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of(
                new Result(1L, 2L, 3, 1),
                new Result(1L, 3L, 0, 0),
                new Result(4L, 1L, 2, 1)));

        Row alpha = row(rows, 1L);
        assertThat(alpha.played()).isEqualTo(3);
        assertThat(alpha.won()).isEqualTo(1);
        assertThat(alpha.drawn()).isEqualTo(1);
        assertThat(alpha.lost()).isEqualTo(1);
        assertThat(alpha.goalsFor()).isEqualTo(4);
        assertThat(alpha.goalsAgainst()).isEqualTo(3);
        assertThat(alpha.goalDifference()).isEqualTo(1);
        assertThat(alpha.points()).isEqualTo(4);
    }

    @Test
    void ordersByPointsFirst() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of(
                new Result(4L, 1L, 1, 0),
                new Result(4L, 2L, 1, 0),
                new Result(3L, 1L, 1, 0)));

        // Delta 6 points, Charlie 3, then Bravo and Alpha on 0 separated by goal difference.
        assertThat(rows).extracting(Row::teamId).containsExactly(4L, 3L, 2L, 1L);
        assertThat(rows).extracting(Row::points).containsExactly(6, 3, 0, 0);
        assertThat(rows).extracting(Row::position).containsExactly(1, 2, 3, 4);
    }

    @Test
    void breaksPointTiesOnGoalDifference() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of(
                new Result(1L, 2L, 5, 0),   // Alpha +5, Bravo -5
                new Result(3L, 4L, 1, 0),   // Charlie +1, Delta -1
                new Result(2L, 3L, 1, 0),   // Bravo +1 (total -4), Charlie -1 (total 0)
                new Result(4L, 1L, 1, 0))); // Delta +1 (total 0), Alpha -1 (total +4)

        // Everybody has won once, so points are level and the goal difference decides.
        assertThat(rows).extracting(Row::points).containsExactly(3, 3, 3, 3);
        assertThat(rows).extracting(Row::goalDifference).containsExactly(4, 0, 0, -4);
        assertThat(rows.get(0).teamId()).isEqualTo(1L);
        assertThat(rows.get(3).teamId()).isEqualTo(2L);
    }

    @Test
    void breaksGoalDifferenceTiesOnGoalsFor() {
        List<Competitor> teams = List.of(
                new Competitor(1L, "Alpha"),
                new Competitor(2L, "Bravo"),
                new Competitor(3L, "Charlie"),
                new Competitor(4L, "Delta"));

        List<Row> rows = Standings.compute(teams, List.of(
                new Result(1L, 3L, 3, 3),   // Alpha: 3-3, 1 point, GD 0
                new Result(2L, 4L, 1, 1))); // Bravo: 1-1, 1 point, GD 0

        // Alpha and Bravo are level on points and goal difference; Alpha scored more.
        assertThat(rows.get(0).teamId()).isEqualTo(1L);
        assertThat(rows.get(0).goalsFor()).isEqualTo(3);
        assertThat(rows.get(1).teamId()).isEqualTo(3L);
        assertThat(rows).extracting(Row::teamId).containsExactly(1L, 3L, 2L, 4L);
    }

    @Test
    void breaksEveryOtherTieOnTheTeamName() {
        List<Competitor> teams = List.of(
                new Competitor(1L, "Zulu"),
                new Competitor(2L, "Whiskey"));

        // A 1-1 draw leaves both teams identical on every counter, so only the name separates them.
        List<Row> rows = Standings.compute(teams, List.of(new Result(1L, 2L, 1, 1)));

        assertThat(rows).extracting(Row::teamName).containsExactly("Whiskey", "Zulu");
        assertThat(rows).extracting(Row::position).containsExactly(1, 2);
    }

    @Test
    void separatesTeamsThatAreIdenticalOnEveryCounterByName() {
        List<Competitor> teams = List.of(
                new Competitor(1L, "Bravo"),
                new Competitor(2L, "Alpha"));

        List<Row> rows = Standings.compute(teams, List.of());

        assertThat(rows).extracting(Row::teamName).containsExactly("Alpha", "Bravo");
    }

    @Test
    void teamsThatHaveNotPlayedYetStillAppearWithAnEmptyRow() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of(new Result(1L, 2L, 1, 0)));

        Row delta = row(rows, 4L);
        assertThat(rows).hasSize(4);
        assertThat(delta.played()).isZero();
        assertThat(delta.points()).isZero();
        assertThat(delta.goalsFor()).isZero();
    }

    @Test
    void ignoresResultsOfTeamsThatAreNotCompetitors() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of(
                new Result(1L, 99L, 5, 0),
                new Result(1L, 2L, 1, 0)));

        assertThat(rows).hasSize(4);
        assertThat(row(rows, 1L).played()).isEqualTo(1);
        assertThat(row(rows, 1L).goalsFor()).isEqualTo(1);
    }

    @Test
    void anEmptyTournamentProducesAnAllZeroTable() {
        List<Row> rows = Standings.compute(FOUR_TEAMS, List.of());

        assertThat(rows).hasSize(4);
        assertThat(rows).allSatisfy(row -> {
            assertThat(row.played()).isZero();
            assertThat(row.points()).isZero();
        });
        assertThat(rows).extracting(Row::position).containsExactly(1, 2, 3, 4);
    }

    private static Row row(List<Row> rows, Long teamId) {
        return rows.stream().filter(row -> row.teamId().equals(teamId)).findFirst().orElseThrow();
    }
}
