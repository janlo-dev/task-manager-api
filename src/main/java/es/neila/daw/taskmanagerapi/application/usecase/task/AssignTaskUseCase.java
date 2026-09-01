package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.AssignTaskRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

import java.util.UUID;

public class AssignTaskUseCase {

    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final BoardAccessChecker boardAccessChecker;
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public AssignTaskUseCase(TaskRepository taskRepository, ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker, UserRepository userRepository, DomainEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.boardAccessChecker = boardAccessChecker;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public Task execute(AssignTaskRequest request, UUID performedByUserId) {
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        boardAccessChecker.verifyCanEditContent(column.getBoardId(), performedByUserId);

        userRepository.findById(request.assignedUserId())
                .orElseThrow(() -> new IllegalArgumentException("User to assign not found"));

        task.assignTo(request.assignedUserId());
        Task savedTask = taskRepository.save(task);

        eventPublisher.publish(new AuditDomainEvent(
                savedTask.getId(),
                "TASK",
                "ASSIGNED",
                performedByUserId,
                "Task assigned to user: " + request.assignedUserId()
        ));

        return savedTask;
    }
}
