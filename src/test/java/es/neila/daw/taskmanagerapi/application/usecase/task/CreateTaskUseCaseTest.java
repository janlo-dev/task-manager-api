package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.CreateTaskRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private CreateTaskUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Column column = new Column(boardId, UUID.randomUUID(), "To do", 0);

    @Test
    void execute_withAccess_createsUnassignedTaskAndPublishesEvent() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(taskRepository.save(any(Task.class))).then(returnsFirstArg());

        Task result = useCase.execute(new CreateTaskRequest("Título", "Desc", column.getId()), userId);

        assertThat(result.getTitle()).isEqualTo("Título");
        assertThat(result.getDescription()).isEqualTo("Desc");
        assertThat(result.getColumnId()).isEqualTo(column.getId());
        assertThat(result.getAssignedUserId()).isNull();

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(result.getId());
        assertThat(event.entityType()).isEqualTo("TASK");
        assertThat(event.action()).isEqualTo("CREATED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownColumn_throws() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new CreateTaskRequest("Título", "Desc", column.getId()), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(taskRepository, eventPublisher);
    }

    @Test
    void execute_withoutAccess_throwsAndSavesNothing() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(new CreateTaskRequest("Título", "Desc", column.getId()), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(taskRepository, eventPublisher);
    }

    @Test
    void execute_withBlankTitle_throwsAndSavesNothing() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        assertThatThrownBy(() -> useCase.execute(new CreateTaskRequest(" ", "Desc", column.getId()), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(taskRepository, eventPublisher);
    }
}
