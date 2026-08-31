package es.neila.daw.taskmanagerapi.application.usecase.audit;

import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

public class GetAuditLogByUserUseCase {

    private final AuditLogRepository auditLogRepository;

    public GetAuditLogByUserUseCase(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> execute(UUID userId) {
        return auditLogRepository.findByPerformedBy(userId);
    }
}
