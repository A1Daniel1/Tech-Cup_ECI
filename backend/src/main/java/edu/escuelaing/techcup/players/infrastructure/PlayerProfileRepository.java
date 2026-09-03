package edu.escuelaing.techcup.players.infrastructure;

import edu.escuelaing.techcup.players.domain.PlayerProfile;
import edu.escuelaing.techcup.players.domain.Position;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {

    @Query("""
            SELECT p FROM PlayerProfile p JOIN FETCH p.user u
            WHERE (:position IS NULL OR p.position = :position)
            ORDER BY u.fullName ASC
            """)
    List<PlayerProfile> findAllByPosition(@Param("position") Position position);

    /**
     * Free agents: active users with a profile that belong to no ACTIVE team. Native SQL because
     * team membership is owned by the teams module (no entity dependency here).
     */
    @Query(value = """
            SELECT p.* FROM player_profiles p
            JOIN users u ON u.id = p.user_id
            WHERE u.status = 'ACTIVE'
              AND (CAST(:position AS VARCHAR) IS NULL OR p.position = CAST(:position AS VARCHAR))
              AND NOT EXISTS (
                  SELECT 1 FROM team_members tm
                  JOIN teams t ON t.id = tm.team_id
                  WHERE tm.user_id = p.user_id AND t.status = 'ACTIVE')
            ORDER BY u.full_name ASC
            """, nativeQuery = true)
    List<PlayerProfile> findFreeAgents(@Param("position") String position);
}
