package es.neila.daw.taskmanagerapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditDomainEvent(

        UUID entityId,
        String entityType, // "TASK", "BOARD", "COLUMN", "USER"
        UUID boardId,      // board al que pertenece la entidad (null en eventos de USER)
        String action,     // "MOVED", "RENAMED", "DELETED", "CREATED"
        UUID performedBy,
        String details,
        LocalDateTime timestamp
) {
    public AuditDomainEvent(UUID entityId, String entityType, UUID boardId, String action, UUID performedBy, String details) {
        this(entityId, entityType, boardId, action, performedBy, details, LocalDateTime.now());
    }
}
