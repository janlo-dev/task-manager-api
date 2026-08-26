package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.audit;

import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.AuditLogMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final SpringDataAuditLogRepository repository;
    private final AuditLogMapper mapper;

    public AuditLogRepositoryAdapter(SpringDataAuditLogRepository repository, AuditLogMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        var entity = mapper.toEntity(auditLog);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<AuditLog> findByEntityId(UUID entityId) {
        return repository.findByEntityIdOrderByTimestampDesc(entityId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByPerformedBy(UUID userId) {
        return repository.findByPerformedByOrderByTimestampDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
