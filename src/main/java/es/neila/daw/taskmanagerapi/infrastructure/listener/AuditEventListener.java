package es.neila.daw.taskmanagerapi.infrastructure.listener;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    public AuditEventListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @EventListener
    public void handleAuditEvent(AuditDomainEvent event) {
        AuditLog log = new AuditLog(
                UUID.randomUUID(),
                event.entityId(),
                event.entityType(),
                event.action(),
                event.performedBy(),
                event.details(),
                event.timestamp()
        );

        auditLogRepository.save(log);
    }
}
