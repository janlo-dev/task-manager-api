package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

import java.util.UUID;


public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;
    private final ColumnRepository columnRepository;
    private final BoardAccessChecker boardAccessChecker;

    public DeleteTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.columnRepository = columnRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    public void execute(UUID taskId, UUID performedByUserId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        boardAccessChecker.verifyCanEditContent(column.getBoardId(), performedByUserId);

        taskRepository.delete(taskId);

        eventPublisher.publish(new AuditDomainEvent(
                taskId,
                "TASK",
                "DELETED",
                performedByUserId,
                "Task deleted successfully"
        ));
    }

}
