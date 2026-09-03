package edu.escuelaing.techcup.competition.application;

import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Knockout {@link FixtureStrategy}: the seeded list is folded in half, so the first seed meets the
 * last one, the second meets the second to last, and so on — the classic bracket where the two
 * strongest sides can only meet in the final.
 *
 * <pre>
 *   8 teams (from the standings): 1v8  2v7  3v6  4v5
 *   4 teams:                      1v4  2v3
 *   2 teams:                      1v2
 * </pre>
 *
 * The same fold is applied again when the winners of a phase move on: with the winners listed in
 * bracket order, folding reproduces the standard semi-final pairing.
 */
@Component
public class KnockoutFixtureStrategy implements FixtureStrategy {

    private static final int MIN_TEAMS = 2;

    /**
     * @param teamIds the qualified teams <b>in seeding order</b> (best first)
     */
    @Override
    public List<Fixture> generate(List<Long> teamIds) {
        int size = teamIds.size();
        if (size < MIN_TEAMS) {
            throw new BusinessRuleException("Se requieren al menos " + MIN_TEAMS
                    + " equipos para generar una fase eliminatoria (se recibieron " + size + ").");
        }
        if (size % 2 != 0) {
            throw new BusinessRuleException("Una fase eliminatoria necesita un número par de equipos (se recibieron " + size + ").");
        }

        List<Fixture> fixtures = new ArrayList<>(size / 2);
        for (int seed = 0; seed < size / 2; seed++) {
            fixtures.add(new Fixture(1, teamIds.get(seed), teamIds.get(size - 1 - seed)));
        }
        return List.copyOf(fixtures);
    }
}
