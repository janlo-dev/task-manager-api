package es.neila.daw.taskmanagerapi.application.usecase.audit;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.domain.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAuditLogByEntityUseCaseTest {

    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private GetAuditLogByEntityUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID entityId = UUID.randomUUID();

    private AuditLog log(String entityType, UUID logBoardId) {
        return new AuditLog(UUID.randomUUID(), entityId, entityType, logBoardId, "CREATED",
                userId, "detalle", LocalDateTime.now());
    }

    @Test
    void execute_withoutLogs_returnsEmptyListWithoutCheckingAccess() {
        when(auditLogRepository.findByEntityId(entityId)).thenReturn(List.of());

        assertThat(useCase.execute(entityId, userId)).isEmpty();
        verifyNoInteractions(boardAccessChecker);
    }

    @Test
    void execute_withAccessToBoard_returnsLogs() {
        // No se consulta si la entidad existe: vale igual para tareas/columnas ya borradas
        List<AuditLog> logs = List.of(log("TASK", boardId), log("TASK", boardId));
        when(auditLogRepository.findByEntityId(entityId)).thenReturn(logs);

        assertThat(useCase.execute(entityId, userId)).isEqualTo(logs);
        verify(boardAccessChecker, times(1)).verifyCanEditContent(boardId, userId);
    }

    @Test
    void execute_withoutAccessToBoard_throwsUnauthorized() {
        when(auditLogRepository.findByEntityId(entityId)).thenReturn(List.of(log("COLUMN", boardId)));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(entityId, userId))
                .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void execute_withOnlyLegacyLogsWithoutBoard_throwsUnauthorized() {
        when(auditLogRepository.findByEntityId(entityId)).thenReturn(List.of(log("TASK", null)));

        assertThatThrownBy(() -> useCase.execute(entityId, userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(boardAccessChecker);
    }

    @Test
    void execute_withMixedLogs_returnsOnlyLogsWithBoard() {
        AuditLog legacy = log("TASK", null);
        AuditLog recent = log("TASK", boardId);
        when(auditLogRepository.findByEntityId(entityId)).thenReturn(List.of(legacy, recent));

        assertThat(useCase.execute(entityId, userId)).containsExactly(recent);
        verify(boardAccessChecker).verifyCanEditContent(boardId, userId);
    }

    @Test
    void execute_forOwnUser_returnsLogs() {
        List<AuditLog> logs = List.of(new AuditLog(UUID.randomUUID(), userId, "USER", null, "RENAMED",
                userId, "detalle", LocalDateTime.now()));
        when(auditLogRepository.findByEntityId(userId)).thenReturn(logs);

        assertThat(useCase.execute(userId, userId)).isEqualTo(logs);
        verifyNoInteractions(boardAccessChecker);
    }

    @Test
    void execute_forAnotherUser_throwsUnauthorized() {
        when(auditLogRepository.findByEntityId(entityId)).thenReturn(List.of(log("USER", null)));

        assertThatThrownBy(() -> useCase.execute(entityId, userId))
                .isInstanceOf(UnauthorizedActionException.class);
    }
}
