package es.neila.daw.taskmanagerapi.application.usecase.audit;

import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

public class GetAuditLogByEntityUseCase {

    private final AuditLogRepository auditLogRepository;

    public GetAuditLogByEntityUseCase(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> execute(UUID entityId) {
        return auditLogRepository.findByEntityId(entityId);
    }
}
