package es.neila.daw.taskmanagerapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditDomainEvent(

        UUID entityId,
        String entityType, // "TASK", "BOARD", "COLUMN"
        String action,     // "MOVED", "RENAMED", "DELETED", "CREATED"
        UUID performedBy,
        String details,
        LocalDateTime timestamp
) {
    public AuditDomainEvent(UUID entityId, String entityType, String action, UUID performedBy, String details) {
        this(entityId, entityType, action, performedBy, details, LocalDateTime.now());
    }
}
