package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.UserTaskProjectionResponse;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GetUserAssignedTasksUseCase {

    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    public GetUserAssignedTasksUseCase(TaskRepository taskRepository, ColumnRepository columnRepository, BoardRepository boardRepository) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
    }

    public List<UserTaskProjectionResponse> execute(UUID userId) {
        List<Task> tasks = taskRepository.findByAssignedUserId(userId);
        List<UserTaskProjectionResponse> result = new ArrayList<>();

        for (Task task : tasks) {
            Column column = columnRepository.findById(task.getColumnId())
                    .orElseThrow(() -> new IllegalArgumentException("Column not found"));

            Board board = boardRepository.findById(column.getBoardId())
                    .orElseThrow(() -> new IllegalArgumentException("Board not found"));

            result.add(new UserTaskProjectionResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    column.getId(),
                    column.getName(),
                    board.getId(),
                    board.getName(),
                    task.getCreatedAt()
            ));
        }

        return result;
    }
}
