package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.RenameTaskRequest;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;


public class RenameTaskUseCase {

    private final TaskRepository taskRepository;

    public RenameTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(RenameTaskRequest request){
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));
        task.rename(request.newTitle());

        return taskRepository.save(task);
    }


}
