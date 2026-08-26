package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.UpdateTaskDescriptionRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

public class UpdateTaskDescriptionUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;

    public UpdateTaskDescriptionUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public Task execute(UpdateTaskDescriptionRequest request){

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));

        task.updateDescription(request.newDescription());

        Task updateTask =  taskRepository.save(task);

        eventPublisher.publish(new AuditDomainEvent(
                updateTask.getId(),
                "TASK",
                "UPDATE",
                null,
                "Task update with description: " + updateTask.getDescription()
        ));
        return updateTask;
    }
}
