package es.neila.daw.taskmanagerapi.application.usecase.audit;

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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAuditLogByUserUseCaseTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private GetAuditLogByUserUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_forOwnActivity_returnsLogsFromRepository() {
        List<AuditLog> logs = List.of(new AuditLog(UUID.randomUUID(), UUID.randomUUID(), "TASK", UUID.randomUUID(), "CREATED",
                userId, "detalle", LocalDateTime.now()));
        when(auditLogRepository.findByPerformedBy(userId)).thenReturn(logs);

        assertThat(useCase.execute(userId, userId)).isEqualTo(logs);
    }

    @Test
    void execute_forAnotherUser_throwsUnauthorizedAndQueriesNothing() {
        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(auditLogRepository);
    }
}
