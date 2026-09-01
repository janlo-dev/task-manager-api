package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.UpdateTaskDescriptionRequest;
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

public class UpdateTaskDescriptionUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;
    private final ColumnRepository columnRepository;
    private final BoardAccessChecker boardAccessChecker;

    public UpdateTaskDescriptionUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.columnRepository = columnRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    public Task execute(UpdateTaskDescriptionRequest request, UUID performedByUserId){

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));

        Column currentColumn = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        boardAccessChecker.verifyCanEditContent(currentColumn.getBoardId(), performedByUserId);

        task.updateDescription(request.newDescription());
        Task updateTask =  taskRepository.save(task);

        eventPublisher.publish(new AuditDomainEvent(
                updateTask.getId(),
                "TASK",
                "UPDATE",
                performedByUserId,
                "Task update with description: " + updateTask.getDescription()
        ));
        return updateTask;
    }
}
