package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.UpdateTaskDescriptionRequest;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

public class UpdateTaskDescriptionUseCase {

    private final TaskRepository taskRepository;

    public UpdateTaskDescriptionUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(UpdateTaskDescriptionRequest request){

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));

        task.updateDescription(request.newDescription());

        return taskRepository.save(task);
    }
}
