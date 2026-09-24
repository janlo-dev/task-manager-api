package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTasksByColumnUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private GetTasksByColumnUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Column column = new Column(boardId, UUID.randomUUID(), "To do", 0);

    @Test
    void execute_withAccess_returnsTasksFromRepository() {
        List<Task> tasks = List.of(new Task(UUID.randomUUID(), "Título", "Desc", null, column.getId()));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(taskRepository.findByColumnIdOrderByCreatedAtAsc(column.getId())).thenReturn(tasks);

        assertThat(useCase.execute(column.getId(), userId)).isEqualTo(tasks);
        verify(boardAccessChecker).verifyCanEditContent(boardId, userId);
    }

    @Test
    void execute_withUnknownColumn_throws() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(column.getId(), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(taskRepository);
    }

    @Test
    void execute_withoutAccess_throwsAndQueriesNoTasks() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(column.getId(), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(taskRepository);
    }
}
