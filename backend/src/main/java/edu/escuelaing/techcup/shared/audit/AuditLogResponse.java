package edu.escuelaing.techcup.shared.audit;

import java.time.Instant;
import java.util.Map;

/** One audit entry as exposed to administrators. {@code actorName} is null for anonymous actions. */
public record AuditLogResponse(
        Long id,
        Long actorUserId,
        String actorName,
        AuditAction action,
        String entityType,
        Long entityId,
        Map<String, Object> details,
        Instant createdAt) {

    static AuditLogResponse from(AuditLog log, String actorName) {
        return new AuditLogResponse(log.getId(), log.getActorUserId(), actorName, log.getAction(),
                log.getEntityType(), log.getEntityId(), log.getDetails(), log.getCreatedAt());
    }
}
