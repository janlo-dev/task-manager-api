package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


public class GetTasksByColumnUseCase {

    private final TaskRepository taskRepository;

    public GetTasksByColumnUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> execute(UUID columnId) {
        return taskRepository.findByColumnId(columnId);
    }

}
