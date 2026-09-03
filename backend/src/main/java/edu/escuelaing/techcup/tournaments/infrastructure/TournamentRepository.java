package edu.escuelaing.techcup.tournaments.infrastructure;

import edu.escuelaing.techcup.tournaments.domain.Tournament;
import edu.escuelaing.techcup.tournaments.domain.TournamentStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findAllByOrderByStartDateDescIdDesc();

    /** Latest live tournament; backs {@code GET /tournaments/current}. */
    Optional<Tournament> findFirstByStatusInOrderByStartDateDescIdDesc(Collection<TournamentStatus> statuses);
}
