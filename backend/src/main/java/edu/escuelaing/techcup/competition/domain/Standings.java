package edu.escuelaing.techcup.competition.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure domain rule: the group-stage table, computed on read from the matches that have been
 * played (nothing is persisted, so a corrected result is reflected immediately).
 *
 * <p>Three points for a win, one for a draw, none for a defeat. Rows are ordered by points,
 * then goal difference, then goals for, then team name; the resulting position is 1-based.
 * Teams that have not played yet still appear, with an all-zero row.</p>
 */
public final class Standings {

    public static final int POINTS_WIN = 3;
    public static final int POINTS_DRAW = 1;

    /** A team taking part in the tournament. */
    public record Competitor(Long teamId, String teamName) {
    }

    /** A finished group-stage match: only the two teams and the score matter here. */
    public record Result(Long homeTeamId, Long awayTeamId, int homeScore, int awayScore) {
    }

    public record Row(
            int position,
            Long teamId,
            String teamName,
            int played,
            int won,
            int drawn,
            int lost,
            int goalsFor,
            int goalsAgainst,
            int goalDifference,
            int points) {
    }

    private Standings() {
    }

    /**
     * @param competitors every team of the tournament, so teams with no match still show up
     * @param results     the PLAYED group-stage matches, in any order
     */
    public static List<Row> compute(List<Competitor> competitors, List<Result> results) {
        Map<Long, Tally> tallies = new LinkedHashMap<>();
        competitors.forEach(competitor -> tallies.put(competitor.teamId(), new Tally(competitor.teamName())));

        for (Result result : results) {
            Tally home = tallies.get(result.homeTeamId());
            Tally away = tallies.get(result.awayTeamId());
            if (home == null || away == null) {
                // A match involving a team that is not a competitor any more is simply ignored.
                continue;
            }
            home.add(result.homeScore(), result.awayScore());
            away.add(result.awayScore(), result.homeScore());
        }

        List<Map.Entry<Long, Tally>> ordered = new ArrayList<>(tallies.entrySet());
        ordered.sort(Comparator
                .comparingInt((Map.Entry<Long, Tally> entry) -> entry.getValue().points).reversed()
                .thenComparing(Comparator.comparingInt((Map.Entry<Long, Tally> entry) ->
                        entry.getValue().goalsFor - entry.getValue().goalsAgainst).reversed())
                .thenComparing(Comparator.comparingInt((Map.Entry<Long, Tally> entry) ->
                        entry.getValue().goalsFor).reversed())
                .thenComparing(entry -> entry.getValue().teamName, String.CASE_INSENSITIVE_ORDER));

        List<Row> rows = new ArrayList<>(ordered.size());
        int position = 1;
        for (Map.Entry<Long, Tally> entry : ordered) {
            Tally tally = entry.getValue();
            rows.add(new Row(position++, entry.getKey(), tally.teamName,
                    tally.won + tally.drawn + tally.lost, tally.won, tally.drawn, tally.lost,
                    tally.goalsFor, tally.goalsAgainst, tally.goalsFor - tally.goalsAgainst, tally.points));
        }
        return List.copyOf(rows);
    }

    private static final class Tally {

        private final String teamName;
        private int won;
        private int drawn;
        private int lost;
        private int goalsFor;
        private int goalsAgainst;
        private int points;

        private Tally(String teamName) {
            this.teamName = teamName;
        }

        private void add(int scored, int conceded) {
            goalsFor += scored;
            goalsAgainst += conceded;
            if (scored > conceded) {
                won++;
                points += POINTS_WIN;
            } else if (scored == conceded) {
                drawn++;
                points += POINTS_DRAW;
            } else {
                lost++;
            }
        }
    }
}
