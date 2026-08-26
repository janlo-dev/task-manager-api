package es.neila.daw.taskmanagerapi.infrastructure.mapper;

import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.audit.AuditLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLog toDomain(AuditLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuditLog(
                entity.getId(),
                entity.getEntityId(),
                entity.getEntityType(),
                entity.getAction(),
                entity.getPerformedBy(),
                entity.getDetails(),
                entity.getTimestamp()
        );
    }

    public AuditLogEntity toEntity(AuditLog domain) {
        if (domain == null) {
            return null;
        }

        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(domain.id());
        entity.setEntityId(domain.entityId());
        entity.setEntityType(domain.entityType());
        entity.setAction(domain.action());
        entity.setPerformedBy(domain.performedBy());
        entity.setDetails(domain.details());
        entity.setTimestamp(domain.timestamp());

        return entity;
    }
}
