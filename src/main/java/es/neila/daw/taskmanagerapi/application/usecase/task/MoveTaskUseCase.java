package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.MoveTaskRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

import java.util.UUID;


public class MoveTaskUseCase {

    private final TaskRepository taskRepository;
    private final DomainEventPublisher eventPublisher;

    public MoveTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public Task execute(MoveTaskRequest request){

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));
        task.moveToColumn(request.newColumnId());

        task.moveToColumn(request.newColumnId());
        Task updatedTask = taskRepository.save(task);

        // Disparamos el evento para la auditoría (Event-Driven)
        // Disparar la auditoría en 1 línea sin alterar tus DTOs
        eventPublisher.publish(new AuditDomainEvent(
                updatedTask.getId(),
                "TASK",
                "MOVED",
                null, // O el ID del usuario si en el futuro lo extraes en la capa de aplicación
                "Task moved to column " + request.newColumnId()
        ));

        return updatedTask;
    }
}
