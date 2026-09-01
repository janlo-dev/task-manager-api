package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.CreateTaskRequest;
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


public class CreateTaskUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;
    private final ColumnRepository columnRepository;
    private final BoardAccessChecker boardAccessChecker;

    public CreateTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.columnRepository = columnRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    public Task execute(CreateTaskRequest request,UUID performedByUserId){

        Column column = columnRepository.findById(request.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        boardAccessChecker.verifyCanEditContent(column.getBoardId(), performedByUserId);

        Task task = new Task(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                null,
                request.columnId()
        );
        Task savedTask = taskRepository.save(task);

        // Publicar evento de auditoría
        eventPublisher.publish(new AuditDomainEvent(
                savedTask.getId(),
                "TASK",
                "CREATED",
                performedByUserId,
                "Task created with title: " + savedTask.getTitle()
        ));

        return savedTask;
    }
}
