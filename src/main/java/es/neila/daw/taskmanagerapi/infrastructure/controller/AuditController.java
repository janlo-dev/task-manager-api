package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.usecase.audit.GetAuditLogByEntityUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.audit.GetAuditLogByUserUseCase;
import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final GetAuditLogByEntityUseCase getAuditLogByEntityUseCase;
    private final GetAuditLogByUserUseCase getAuditLogByUserUseCase;

    public AuditController(GetAuditLogByEntityUseCase getAuditLogByEntityUseCase, GetAuditLogByUserUseCase getAuditLogByUserUseCase) {
        this.getAuditLogByEntityUseCase = getAuditLogByEntityUseCase;
        this.getAuditLogByUserUseCase = getAuditLogByUserUseCase;
    }

    @GetMapping("/entity/{entityId}")
    public List<AuditLog> getByEntity(@PathVariable UUID entityId) {
        return getAuditLogByEntityUseCase.execute(entityId);
    }

    @GetMapping("/user/{userId}")
    public List<AuditLog> getByUser(@PathVariable UUID userId) {
        return getAuditLogByUserUseCase.execute(userId);
    }
}
