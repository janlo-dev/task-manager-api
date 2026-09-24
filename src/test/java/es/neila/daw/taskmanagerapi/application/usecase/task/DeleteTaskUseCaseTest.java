package es.neila.daw.taskmanagerapi.application.usecase.task;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private DeleteTaskUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Column column = new Column(boardId, UUID.randomUUID(), "To do", 0);
    private final Task task = new Task(UUID.randomUUID(), "Título", "Desc", null, column.getId());

    @Test
    void execute_withAccess_deletesAndPublishesEvent() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        useCase.execute(task.getId(), userId);

        verify(taskRepository).delete(task.getId());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(task.getId());
        assertThat(event.entityType()).isEqualTo("TASK");
        assertThat(event.action()).isEqualTo("DELETED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownTask_throws() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(task.getId(), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withUnknownColumn_throws() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(task.getId(), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withoutAccess_throwsAndDeletesNothing() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(task.getId(), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verify(taskRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }
}
