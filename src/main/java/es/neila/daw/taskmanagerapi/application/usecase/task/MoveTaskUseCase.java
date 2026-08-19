package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.MoveTaskRequest;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;


public class MoveTaskUseCase {

    private final TaskRepository taskRepository;

    public MoveTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(MoveTaskRequest request){

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(()-> new IllegalArgumentException("Task not found"));
        task.moveToColumn(request.newColumnId());

        return taskRepository.save(task);
    }
}
