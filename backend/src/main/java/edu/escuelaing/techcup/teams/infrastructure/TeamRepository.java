package edu.escuelaing.techcup.teams.infrastructure;

import edu.escuelaing.techcup.teams.domain.Team;
import edu.escuelaing.techcup.teams.domain.TeamStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Team> findByNameIgnoreCase(String name);

    List<Team> findAllByOrderByNameAsc();

    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.id.userId = :userId AND t.status = :status")
    Optional<Team> findTeamByMemberAndStatus(@Param("userId") Long userId, @Param("status") TeamStatus status);

    /** The single ACTIVE team a user belongs to (the service guarantees at most one). */
    default Optional<Team> findActiveTeamByMember(Long userId) {
        return findTeamByMemberAndStatus(userId, TeamStatus.ACTIVE);
    }
}
