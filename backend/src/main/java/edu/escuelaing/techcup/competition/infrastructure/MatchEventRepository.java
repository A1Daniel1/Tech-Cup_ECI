package edu.escuelaing.techcup.competition.infrastructure;

import edu.escuelaing.techcup.competition.domain.EventType;
import edu.escuelaing.techcup.competition.domain.MatchEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {

    /** One row per scorer of the tournament, ordered by goals descending then name. */
    @Query("""
            SELECT e.player.id, e.player.fullName, e.team.id, e.team.name, COUNT(e)
            FROM MatchEvent e
            WHERE e.match.tournament.id = :tournamentId
              AND e.type = edu.escuelaing.techcup.competition.domain.EventType.GOAL
              AND e.match.status = edu.escuelaing.techcup.competition.domain.MatchStatus.PLAYED
            GROUP BY e.player.id, e.player.fullName, e.team.id, e.team.name
            ORDER BY COUNT(e) DESC, e.player.fullName ASC
            """)
    List<Object[]> findTopScorers(@Param("tournamentId") Long tournamentId);

    /** Events of a given type for one player in one tournament, oldest match first. */
    @Query("""
            SELECT e FROM MatchEvent e
            WHERE e.match.tournament.id = :tournamentId
              AND e.player.id = :playerId
              AND e.type = :type
              AND e.match.status = edu.escuelaing.techcup.competition.domain.MatchStatus.PLAYED
            ORDER BY e.match.scheduledAt ASC, e.match.id ASC
            """)
    List<MatchEvent> findPlayerEvents(@Param("tournamentId") Long tournamentId,
                                      @Param("playerId") Long playerId,
                                      @Param("type") EventType type);
}
