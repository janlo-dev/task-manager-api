package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLogEntity {

    @Id
    private UUID id;
    private UUID entityId;
    private String entityType;
    private String action;
    private UUID performedBy;
    private String details;
    private LocalDateTime timestamp;
}
