package es.neila.daw.taskmanagerapi.infrastructure.config;

import es.neila.daw.taskmanagerapi.application.usecase.task.*;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository) {
        return new CreateTaskUseCase(taskRepository);
    }

    @Bean
    public MoveTaskUseCase moveTaskUseCase(TaskRepository taskRepository) {
        return new MoveTaskUseCase(taskRepository);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository) {
        return new DeleteTaskUseCase(taskRepository);
    }

    @Bean
    public GetTasksByColumnUseCase getTasksByColumnUseCase(TaskRepository taskRepository) {
        return new GetTasksByColumnUseCase(taskRepository);
    }

    @Bean
    public RenameTaskUseCase renameTaskUseCase(TaskRepository taskRepository) {
        return new RenameTaskUseCase(taskRepository);
    }

    @Bean
    public UpdateTaskDescriptionUseCase updateTaskDescriptionUseCase(TaskRepository taskRepository) {
        return new UpdateTaskDescriptionUseCase(taskRepository);
    }

}
