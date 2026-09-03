package edu.escuelaing.techcup.competition.infrastructure;

import edu.escuelaing.techcup.competition.domain.Lineup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineupRepository extends JpaRepository<Lineup, Long> {

    Optional<Lineup> findByMatchIdAndTeamId(Long matchId, Long teamId);
}
