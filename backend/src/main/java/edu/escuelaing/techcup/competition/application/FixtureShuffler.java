package edu.escuelaing.techcup.competition.application;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The source of randomness used when drawing the group stage, isolated in its own bean so the
 * fixture generator stays deterministic under test: production uses a {@link SecureRandom},
 * tests pass a seeded {@link Random} and always get the same draw.
 */
@Component
public class FixtureShuffler {

    private final Random random;

    @Autowired
    public FixtureShuffler() {
        this(new SecureRandom());
    }

    /** Seedable constructor for tests. */
    public FixtureShuffler(Random random) {
        this.random = random;
    }

    /** A shuffled copy; the input is never modified. */
    public <T> List<T> shuffled(Collection<T> items) {
        List<T> copy = new ArrayList<>(items);
        Collections.shuffle(copy, random);
        return copy;
    }
}
