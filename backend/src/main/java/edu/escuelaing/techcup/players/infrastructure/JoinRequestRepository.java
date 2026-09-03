package edu.escuelaing.techcup.players.infrastructure;

import edu.escuelaing.techcup.players.domain.JoinRequest;
import edu.escuelaing.techcup.players.domain.JoinRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    boolean existsByPlayerIdAndStatus(Long playerId, JoinRequestStatus status);

    List<JoinRequest> findByPlayerIdOrderByCreatedAtDesc(Long playerId);

    List<JoinRequest> findByPlayerIdAndStatus(Long playerId, JoinRequestStatus status);

    List<JoinRequest> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    List<JoinRequest> findByTeamIdAndStatusOrderByCreatedAtDesc(Long teamId, JoinRequestStatus status);
}
