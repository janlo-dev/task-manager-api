package es.neila.daw.taskmanagerapi.application.usecase.audit;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

public class GetAuditLogByEntityUseCase {

    private static final String NO_ACCESS = "You don't have access to this entity";

    private final AuditLogRepository auditLogRepository;
    private final BoardAccessChecker boardAccessChecker;

    public GetAuditLogByEntityUseCase(AuditLogRepository auditLogRepository, BoardAccessChecker boardAccessChecker) {
        this.auditLogRepository = auditLogRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    // El acceso se comprueba con el boardId guardado en cada registro, así funciona
    // aunque la entidad ya se haya borrado. Los registros antiguos sin boardId se deniegan.
    public List<AuditLog> execute(UUID entityId, UUID performedByUserId) {
        List<AuditLog> logs = auditLogRepository.findByEntityId(entityId);
        if (logs.isEmpty()) {
            return logs;
        }

        if ("USER".equalsIgnoreCase(logs.get(0).entityType())) {
            if (!entityId.equals(performedByUserId)) {
                throw new UnauthorizedActionException(NO_ACCESS);
            }
            return logs;
        }

        List<AuditLog> logsWithBoard = logs.stream()
                .filter(log -> log.boardId() != null)
                .toList();
        if (logsWithBoard.isEmpty()) {
            throw new UnauthorizedActionException(NO_ACCESS);
        }

        logsWithBoard.stream()
                .map(AuditLog::boardId)
                .distinct()
                .forEach(boardId -> boardAccessChecker.verifyCanEditContent(boardId, performedByUserId));

        return logsWithBoard;
    }
}
