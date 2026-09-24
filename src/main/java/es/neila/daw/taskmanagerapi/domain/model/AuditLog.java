package es.neila.daw.taskmanagerapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLog(
        UUID id,
        UUID entityId,
        String entityType, // "TASK", "COLUMN", "BOARD", "USER"
        UUID boardId,      // null en registros de USER y en los anteriores a guardar el board
        String action,     // "MOVED", "RENAMED", "CREATED", "DELETED"
        UUID performedBy,
        String details,
        LocalDateTime timestamp
) {
}
