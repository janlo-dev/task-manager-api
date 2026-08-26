package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.RenameTaskRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;


public class RenameTaskUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;

    public RenameTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public Task execute(RenameTaskRequest request){
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));
        task.rename(request.newTitle());

        Task renameTask = taskRepository.save(task);

        eventPublisher.publish(new AuditDomainEvent(
                renameTask.getId(),
                "TASK",
                "UPDATE",
                null,
                "Task UPDATE with NAME: " + renameTask.getTitle()
        ));

        return renameTask;
    }


}
