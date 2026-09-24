package es.neila.daw.taskmanagerapi.application.usecase.audit;

import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

public class GetAuditLogByUserUseCase {

    private final AuditLogRepository auditLogRepository;

    public GetAuditLogByUserUseCase(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // La actividad de un usuario abarca varios boards: solo la puede consultar él mismo
    public List<AuditLog> execute(UUID userId, UUID performedByUserId) {
        if (!userId.equals(performedByUserId)) {
            throw new UnauthorizedActionException("You can only see your own activity");
        }
        return auditLogRepository.findByPerformedBy(userId);
    }
}
