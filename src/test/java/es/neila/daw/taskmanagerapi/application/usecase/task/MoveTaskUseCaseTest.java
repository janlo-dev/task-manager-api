package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.MoveTaskRequest;
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
class MoveTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private MoveTaskUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Column source = new Column(boardId, UUID.randomUUID(), "To do", 0);
    private final Column destination = new Column(boardId, UUID.randomUUID(), "Done", 1);
    private final Task task = new Task(UUID.randomUUID(), "Título", "Desc", null, source.getId());
    private final MoveTaskRequest request = new MoveTaskRequest(task.getId(), destination.getId());

    @Test
    void execute_withinSameBoard_movesTaskAndPublishesEvent() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(source.getId())).thenReturn(Optional.of(source));
        when(columnRepository.findById(destination.getId())).thenReturn(Optional.of(destination));
        when(taskRepository.save(any(Task.class))).then(returnsFirstArg());

        Task result = useCase.execute(request, userId);

        assertThat(result.getColumnId()).isEqualTo(destination.getId());
        verify(boardAccessChecker).verifyCanEditContent(boardId, userId);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityType()).isEqualTo("TASK");
        assertThat(event.action()).isEqualTo("MOVED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownTask_throws() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withoutAccessToSourceBoard_throwsAndSavesNothing() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(source.getId())).thenReturn(Optional.of(source));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(request, userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withUnknownDestinationColumn_throwsAndSavesNothing() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(source.getId())).thenReturn(Optional.of(source));
        when(columnRepository.findById(destination.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Destination column not found");

        assertThat(task.getColumnId()).isEqualTo(source.getId());
        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withDestinationInAnotherBoard_throwsAndSavesNothing() {
        Column otherBoardColumn = new Column(UUID.randomUUID(), UUID.randomUUID(), "Done", 0);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(source.getId())).thenReturn(Optional.of(source));
        when(columnRepository.findById(otherBoardColumn.getId())).thenReturn(Optional.of(otherBoardColumn));

        assertThatThrownBy(() -> useCase.execute(new MoveTaskRequest(task.getId(), otherBoardColumn.getId()), userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot move a task to a column of another board");

        assertThat(task.getColumnId()).isEqualTo(source.getId());
        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
