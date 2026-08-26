package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    List<AuditLogEntity> findByEntityIdOrderByTimestampDesc(UUID entityId);
    List<AuditLogEntity> findByPerformedByOrderByTimestampDesc(UUID userId);
}
