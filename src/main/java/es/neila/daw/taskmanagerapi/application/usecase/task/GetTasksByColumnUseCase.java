package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

import java.util.List;
import java.util.UUID;


public class GetTasksByColumnUseCase {

    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final BoardAccessChecker boardAccessChecker;

    public GetTasksByColumnUseCase(TaskRepository taskRepository, ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    public List<Task> execute(UUID columnId, UUID performedByUserId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        boardAccessChecker.verifyCanEditContent(column.getBoardId(), performedByUserId);

        return taskRepository.findByColumnIdOrderByCreatedAtAsc(columnId);
    }

}
