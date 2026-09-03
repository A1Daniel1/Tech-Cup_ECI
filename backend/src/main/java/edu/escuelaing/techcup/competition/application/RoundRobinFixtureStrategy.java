package edu.escuelaing.techcup.competition.application;

import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Group-stage {@link FixtureStrategy}: a single round robin where every team meets every other
 * team exactly once.
 *
 * <p>The draw is shuffled first ({@link FixtureShuffler}) so the calendar is not a function of the
 * registration order, then the shuffled list is split into matchdays with the <b>circle
 * method</b>: the first slot is pinned and the remaining slots rotate one position per round, so
 * {@code n} teams produce {@code n - 1} rounds of {@code n / 2} matches. An odd number of teams
 * gets a {@code null} "bye" slot, whose pairing is skipped, which gives each team exactly one
 * rest day.</p>
 *
 * <p>Home advantage alternates along both the round and the slot index so no team plays every
 * fixture at home.</p>
 */
@Component
public class RoundRobinFixtureStrategy implements FixtureStrategy {

    private static final int MIN_TEAMS = 2;

    private final FixtureShuffler shuffler;

    public RoundRobinFixtureStrategy(FixtureShuffler shuffler) {
        this.shuffler = shuffler;
    }

    @Override
    public List<Fixture> generate(List<Long> teamIds) {
        if (teamIds.size() < MIN_TEAMS) {
            throw new BusinessRuleException("Se requieren al menos " + MIN_TEAMS
                    + " equipos para generar la fase de grupos (se recibieron " + teamIds.size() + ").");
        }

        List<Long> slots = new ArrayList<>(shuffler.shuffled(teamIds));
        if (slots.size() % 2 != 0) {
            slots.add(null); // bye: the team facing it rests this round
        }
        int size = slots.size();
        int rounds = size - 1;
        int matchesPerRound = size / 2;

        List<Fixture> fixtures = new ArrayList<>(rounds * matchesPerRound);
        for (int round = 0; round < rounds; round++) {
            for (int slot = 0; slot < matchesPerRound; slot++) {
                Long first = slots.get(slot);
                Long second = slots.get(size - 1 - slot);
                if (first == null || second == null) {
                    continue;
                }
                boolean swapped = (round + slot) % 2 == 1;
                fixtures.add(new Fixture(round + 1,
                        swapped ? second : first,
                        swapped ? first : second));
            }
            // Rotate every slot but the first one, which stays pinned.
            slots.add(1, slots.remove(size - 1));
        }
        return List.copyOf(fixtures);
    }
}
