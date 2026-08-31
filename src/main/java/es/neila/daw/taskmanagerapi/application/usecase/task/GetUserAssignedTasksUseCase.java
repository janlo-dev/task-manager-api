package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.UserTaskProjectionResponse;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;

import java.util.List;
import java.util.UUID;


public class GetUserAssignedTasksUseCase {

    private final TaskRepository taskRepository;

    public GetUserAssignedTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<UserTaskProjectionResponse> execute(UUID userId) {
        return taskRepository.findTasksByAssignedUserId(userId);
    }
}
