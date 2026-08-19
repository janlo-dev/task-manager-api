package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(UUID taskId) {
        if (taskRepository.findById(taskId).isEmpty()) {
            throw new IllegalArgumentException("La tarea con ID " + taskId + " no existe");
        }
        taskRepository.delete(taskId);
    }

}
