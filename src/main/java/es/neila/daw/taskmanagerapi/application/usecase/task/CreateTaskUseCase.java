package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.CreateTaskRequest;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;


public class CreateTaskUseCase {

    private final TaskRepository taskRepository;

    public CreateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(CreateTaskRequest request){
        Task task = new Task(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                request.columnId()
        );
        return taskRepository.save(task);
    }
}
