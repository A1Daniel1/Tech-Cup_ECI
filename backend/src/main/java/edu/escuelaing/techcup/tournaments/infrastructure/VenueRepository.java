package edu.escuelaing.techcup.tournaments.infrastructure;

import edu.escuelaing.techcup.tournaments.domain.Venue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByTournamentIdOrderByIdAsc(Long tournamentId);
}
