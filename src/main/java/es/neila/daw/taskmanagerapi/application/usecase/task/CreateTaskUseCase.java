package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.CreateTaskRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;


public class CreateTaskUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public Task execute(CreateTaskRequest request){
        Task task = new Task(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                request.columnId()
        );
        Task savedTask = taskRepository.save(task);

        // Publicar evento de auditoría
        eventPublisher.publish(new AuditDomainEvent(
                savedTask.getId(),
                "TASK",
                "CREATED",
                null,
                "Task created with title: " + savedTask.getTitle()
        ));

        return savedTask;
    }
}
