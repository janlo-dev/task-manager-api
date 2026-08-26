package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByEntityId(UUID entityId);
    List<AuditLog> findByPerformedBy(UUID userId);
}
