package edu.escuelaing.techcup.shared.audit;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditRepository extends JpaRepository<AuditLog, Long> {

    /** Projection used to resolve actor names without depending on the identity module. */
    interface ActorName {
        Long getId();

        String getFullName();
    }

    List<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action, Pageable pageable);

    @Query(value = "SELECT u.id AS id, u.full_name AS fullName FROM users u WHERE u.id IN (:ids)",
            nativeQuery = true)
    List<ActorName> findActorNames(@Param("ids") Collection<Long> ids);
}
