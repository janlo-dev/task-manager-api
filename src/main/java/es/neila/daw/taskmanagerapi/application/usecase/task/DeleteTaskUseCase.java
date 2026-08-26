package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

import java.util.UUID;


public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;

    public DeleteTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(UUID taskId, UUID performedByUserId) {
        if (taskRepository.findById(taskId).isEmpty()) {
            throw new IllegalArgumentException("La tarea con ID " + taskId + " no existe");
        }
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
