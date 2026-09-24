package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.UserTaskProjectionResponse;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserAssignedTasksUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private GetUserAssignedTasksUseCase useCase;

    private final UUID userId = UUID.randomUUID();
    private final Board board = new Board(UUID.randomUUID(), UUID.randomUUID(), "Tablero", 0);
    private final Column column = new Column(board.getId(), UUID.randomUUID(), "To do", 0);
    private final Task task = new Task(UUID.randomUUID(), "Título", "Desc", userId, column.getId());

    @Test
    void execute_buildsProjectionWithTaskColumnAndBoardData() {
        when(taskRepository.findByAssignedUserId(userId)).thenReturn(List.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        List<UserTaskProjectionResponse> result = useCase.execute(userId);

        assertThat(result).containsExactly(new UserTaskProjectionResponse(
                task.getId(), "Título", "Desc",
                column.getId(), "To do",
                board.getId(), "Tablero",
                task.getCreatedAt()));
    }

    @Test
    void execute_withoutAssignedTasks_returnsEmptyList() {
        when(taskRepository.findByAssignedUserId(userId)).thenReturn(List.of());

        assertThat(useCase.execute(userId)).isEmpty();
        verifyNoInteractions(columnRepository, boardRepository);
    }

    @Test
    void execute_withMissingColumn_throws() {
        when(taskRepository.findByAssignedUserId(userId)).thenReturn(List.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column not found");
    }

    @Test
    void execute_withMissingBoard_throws() {
        when(taskRepository.findByAssignedUserId(userId)).thenReturn(List.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(boardRepository.findById(board.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Board not found");
    }
}
